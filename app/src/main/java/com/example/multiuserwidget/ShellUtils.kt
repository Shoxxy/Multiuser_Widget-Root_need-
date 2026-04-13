package com.example.multiuserwidget

import java.io.BufferedReader
import java.io.InputStreamReader

object ShellUtils {
    private var hasRootAccess: Boolean? = null

    fun checkRootAccess(): Boolean {
        if (hasRootAccess != null) return hasRootAccess!!
        val result = executeCommand("id")
        hasRootAccess = result.contains("uid=0")
        return hasRootAccess!!
    }

    fun executeCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            process.waitFor()
            output.toString()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun saveGlobalConfig(enabledIds: Set<String>) {
        val idsStr = enabledIds.joinToString(",")
        // Use double escaping for the shell command to handle the echo correctly through su -c
        executeCommand("echo \"$idsStr\" > /data/local/tmp/multiuser_config.txt && chmod 666 /data/local/tmp/multiuser_config.txt")
    }

    fun readGlobalConfig(): Set<String> {
        val result = executeCommand("cat /data/local/tmp/multiuser_config.txt")
        if (result.isBlank() || result.startsWith("Error:")) return setOf()
        // Clean up any quotes or whitespace from the echo command
        val clean = result.replace("\"", "").replace("\n", "").replace("\r", "").trim()
        return if (clean.isEmpty()) setOf() else clean.split(",").toSet()
    }

    fun switchUser(userId: Int): String {
        return executeCommand("am switch-user $userId")
    }

    fun deployToAllUsers(): List<String> {
        val users = listUsers()
        val results = mutableListOf<String>()
        val packageName = "com.example.multiuserwidget"
        users.forEach { user ->
            if (user.id != 0) { // Root/Owner usually already has it
                val res = executeCommand("pm install-existing --user ${user.id} $packageName")
                results.add("User ${user.id}: $res")
            }
        }
        return results
    }

    fun listUsers(): List<UserInfo> {
        val output = executeCommand("pm list users")
        val users = mutableListOf<UserInfo>()
        // Format: UserInfo{0:Owner:13} or similar
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
        return users
    }

    fun getCurrentUser(): Int {
        val output = executeCommand("am get-current-user")
        return output.trim().toIntOrNull() ?: 0
    }

    fun getUserAvatarPath(context: android.content.Context, userId: Int): String? {
        val destFile = java.io.File(context.cacheDir, "avatar_$userId.png")
        val check = executeCommand("ls /data/system/users/$userId/photo.png")
        if (check.contains("No such file") || check.contains("No such") || check.isBlank()) {
            if (destFile.exists()) destFile.delete()
            return null
        }
        executeCommand("cp /data/system/users/$userId/photo.png ${destFile.absolutePath} && chmod 644 ${destFile.absolutePath}")
        return if (destFile.exists() && destFile.length() > 0) destFile.absolutePath else null
    }
}

data class UserInfo(val id: Int, val name: String)
