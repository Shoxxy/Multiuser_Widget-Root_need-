package com.example.multiuserwidget

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object ShellUtils {
    private var hasRootAccess: Boolean? = null
    
    // Simple TTL Cache
    private var cachedUsers: List<UserInfo>? = null
    private var cachedCurrentUser: Int? = null
    private var lastCacheTime: Long = 0
    private const val CACHE_TTL = 3000L // 3 seconds cache

    fun checkRootAccess(): Boolean {
        if (hasRootAccess != null) return hasRootAccess!!
        val result = executeCommand("id", timeoutMs = 2000)
        hasRootAccess = result.contains("uid=0")
        return hasRootAccess!!
    }

    fun executeCommand(command: String, timeoutMs: Long = 5000): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            
            val output = StringBuilder()
            val thread = Thread {
                try {
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        output.append(line).append("\n")
                    }
                } catch (e: Exception) {}
            }
            thread.start()
            
            // Wait with timeout
            val exited = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                process.waitFor(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
            } else {
                process.waitFor()
                true
            }

            if (!exited) {
                process.destroy()
                return "Error: Command timed out after ${timeoutMs}ms"
            }
            
            thread.join(500) // Small wait for buffer flush
            output.toString()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun saveGlobalConfig(enabledIds: Set<String>) {
        val idsStr = enabledIds.joinToString(",")
        executeCommand("echo \"$idsStr\" > /data/local/tmp/multiuser_config.txt && chmod 666 /data/local/tmp/multiuser_config.txt")
    }

    fun readGlobalConfig(): Set<String> {
        val result = executeCommand("cat /data/local/tmp/multiuser_config.txt")
        if (result.isBlank() || result.startsWith("Error:")) return setOf()
        val clean = result.replace("\"", "").replace("\n", "").replace("\r", "").trim()
        return if (clean.isEmpty()) setOf() else clean.split(",").toSet()
    }

    fun switchUser(userId: Int): String {
        // Clear cache on switch
        cachedCurrentUser = null
        cachedUsers = null
        return executeCommand("am switch-user $userId")
    }

    fun deployToAllUsers(): List<String> {
        val users = listUsers()
        val results = mutableListOf<String>()
        val packageName = "com.example.multiuserwidget"
        users.forEach { user ->
            if (user.id != 0) {
                val res = executeCommand("pm install-existing --user ${user.id} $packageName")
                results.add("User ${user.id}: $res")
            }
        }
        return results
    }

    fun listUsers(): List<UserInfo> {
        val now = System.currentTimeMillis()
        if (cachedUsers != null && (now - lastCacheTime) < CACHE_TTL) {
            return cachedUsers!!
        }

        val output = executeCommand("pm list users")
        Log.d("ShellUtils", "pm list users output: $output")
        val users = mutableListOf<UserInfo>()
        val regex = Regex("""UserInfo\{(\d+):([^:]+):([^}]+)\}""")
        output.lines().forEach { line ->
            regex.find(line)?.let { match ->
                val id = match.groups[1]?.value?.toIntOrNull() ?: -1
                val name = match.groups[2]?.value ?: "Unknown"
                if (id != -1) {
                    users.add(UserInfo(id, name))
                }
            }
        }
        
        if (users.isNotEmpty()) {
            cachedUsers = users
            lastCacheTime = now
            Log.d("ShellUtils", "Parsed ${users.size} users")
        } else {
            Log.w("ShellUtils", "No users parsed from output")
        }
        return users
    }

    fun getCurrentUser(): Int {
        val now = System.currentTimeMillis()
        if (cachedCurrentUser != null && (now - lastCacheTime) < CACHE_TTL) {
            return cachedCurrentUser!!
        }

        val output = executeCommand("am get-current-user")
        Log.d("ShellUtils", "am get-current-user output: $output")
        val id = output.trim().toIntOrNull() ?: 0
        cachedCurrentUser = id
        return id
    }

    fun getUserAvatarPath(context: android.content.Context, userId: Int): String? {
        val destFile = java.io.File(context.cacheDir, "avatar_$userId.png")
        
        // Cache avatar path for 1 minute
        if (destFile.exists() && destFile.length() > 0 && (System.currentTimeMillis() - destFile.lastModified()) < 60000) {
            return destFile.absolutePath
        }

        val check = executeCommand("ls /data/system/users/$userId/photo.png", timeoutMs = 1000)
        if (check.contains("No such file") || check.contains("No such") || check.isBlank() || check.startsWith("Error:")) {
            if (destFile.exists()) destFile.delete()
            return null
        }
        
        executeCommand("cp /data/system/users/$userId/photo.png ${destFile.absolutePath} && chmod 644 ${destFile.absolutePath}", timeoutMs = 2000)
        return if (destFile.exists() && destFile.length() > 0) destFile.absolutePath else null
    }
}

data class UserInfo(val id: Int, val name: String)
