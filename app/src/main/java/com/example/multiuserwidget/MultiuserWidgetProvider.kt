package com.example.multiuserwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

open class MultiuserWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_SWITCH_USER = "com.example.multiuserwidget.ACTION_SWITCH_USER"
        const val EXTRA_USER_ID = "extra_user_id"
        private const val TAG = "MultiuserWidget"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, this)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_SWITCH_USER) {
            val userId = intent.getIntExtra(EXTRA_USER_ID, -1)
            if (userId != -1) {
                Log.d(TAG, "Switching to user $userId")
                ShellUtils.switchUser(userId)
            }
        }
    }
}

data class WidgetSlot(val rootId: Int, val circleId: Int, val iconId: Int, val nameId: Int)

internal fun updateAppWidget(
    context: Context, 
    appWidgetManager: AppWidgetManager, 
    appWidgetId: Int, 
    provider: MultiuserWidgetProvider
) {
    val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
    val baseProvider = provider as? BaseMultiuserWidgetProvider
    val layoutRes = baseProvider?.layoutResId ?: R.layout.widget_neon_bar
    val views = RemoteViews(context.packageName, layoutRes)

    val currentUser = ShellUtils.getCurrentUser()
    val allUsers = ShellUtils.listUsers()

    val isQuantum = layoutRes == R.layout.widget_quantum_bar || 
                   layoutRes == R.layout.widget_quantum_grid || 
                   layoutRes == R.layout.widget_quantum_expand

    when (layoutRes) {
        R.layout.widget_neon_bar, R.layout.widget_quantum_bar,
        R.layout.widget_bar_pill, R.layout.widget_bar_glass -> {
            val hubIdsStr = prefs.getString("hub_$appWidgetId", "") ?: ""
            val usersToShow = if (hubIdsStr.isNotEmpty()) {
                val selectedIds = hubIdsStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
                allUsers.filter { it.id in selectedIds }.take(6)
            } else {
                allUsers.take(6)
            }
            
            // Flexible scaling factor based on user count
            val scaleFactor = when(usersToShow.size) {
                1 -> 0.95f
                2 -> 0.85f
                3 -> 0.75f
                4 -> 0.65f
                else -> 0.55f
            }

            val slots = listOf(
                WidgetSlot(R.id.user_1_root, R.id.user_1_circle, R.id.user_1_icon, R.id.user_1_name),
                WidgetSlot(R.id.user_2_root, R.id.user_2_circle, R.id.user_2_icon, R.id.user_2_name),
                WidgetSlot(R.id.user_3_root, R.id.user_3_circle, R.id.user_3_icon, R.id.user_3_name),
                WidgetSlot(R.id.user_4_root, R.id.user_4_circle, R.id.user_4_icon, R.id.user_4_name),
                WidgetSlot(R.id.user_5_root, R.id.user_5_circle, R.id.user_5_icon, R.id.user_5_name),
                WidgetSlot(R.id.user_6_root, R.id.user_6_circle, R.id.user_6_icon, R.id.user_6_name)
            )
            slots.forEachIndexed { i, slot ->
                if (i < usersToShow.size) {
                    val user = usersToShow[i]
                    views.setViewVisibility(slot.rootId, android.view.View.VISIBLE)
                    updateUserItem(context, views, slot, user, currentUser, appWidgetId, provider::class.java, isQuantum = isQuantum, scaleFactor = scaleFactor)
                } else {
                    views.setViewVisibility(slot.rootId, android.view.View.GONE)
                }
            }
        }
        R.layout.widget_neon_grid, R.layout.widget_quantum_grid -> {
            val hubIdsStr = prefs.getString("hub_$appWidgetId", "") ?: ""
            val effectiveUsers = if (hubIdsStr.isNotEmpty()) {
                val selectedIds = hubIdsStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
                allUsers.filter { it.id in selectedIds }
            } else {
                allUsers
            }
            
            val activeUser = effectiveUsers.find { it.id == currentUser } ?: effectiveUsers.firstOrNull()
            val others = effectiveUsers.filter { it.id != activeUser?.id }.take(8)
            val totalCount = others.size + (if (activeUser != null) 1 else 0)

            val scaleFactor = when {
                totalCount <= 2 -> 0.95f
                totalCount <= 4 -> 0.75f
                else -> 0.55f
            }

            activeUser?.let { 
                val mainSlot = WidgetSlot(R.id.user_active_root, R.id.user_active_circle, R.id.user_active_icon, R.id.user_active_name)
                updateUserItem(context, views, mainSlot, it, currentUser, appWidgetId, provider::class.java, true, isQuantum, scaleFactor = scaleFactor)
            }
            
            val otherSlots = listOf(
                WidgetSlot(R.id.user_2_root, R.id.user_2_circle, R.id.user_2_icon, R.id.user_2_name),
                WidgetSlot(R.id.user_3_root, R.id.user_3_circle, R.id.user_3_icon, R.id.user_3_name),
                WidgetSlot(R.id.user_4_root, R.id.user_4_circle, R.id.user_4_icon, R.id.user_4_name),
                WidgetSlot(R.id.user_5_root, R.id.user_5_circle, R.id.user_5_icon, R.id.user_5_name),
                WidgetSlot(R.id.user_6_root, R.id.user_6_circle, R.id.user_6_icon, R.id.user_6_name),
                WidgetSlot(R.id.user_7_root, R.id.user_7_circle, R.id.user_7_icon, R.id.user_7_name),
                WidgetSlot(R.id.user_8_root, R.id.user_8_circle, R.id.user_8_icon, R.id.user_8_name),
                WidgetSlot(R.id.user_9_root, R.id.user_9_circle, R.id.user_9_icon, R.id.user_9_name)
            )
            otherSlots.forEachIndexed { i, slot ->
                if (i < others.size) {
                    views.setViewVisibility(slot.rootId, android.view.View.VISIBLE)
                    updateUserItem(context, views, slot, others[i], currentUser, appWidgetId, provider::class.java, isQuantum = isQuantum, scaleFactor = scaleFactor)
                } else {
                    views.setViewVisibility(slot.rootId, android.view.View.GONE)
                }
            }
        }
        else -> {
            // Simplified handling for everything else
            val hubIdsStr = prefs.getString("hub_$appWidgetId", "") ?: ""
            val usersToShow = if (hubIdsStr.isNotEmpty()) {
                val selectedIds = hubIdsStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
                allUsers.filter { it.id in selectedIds }.take(6)
            } else {
                allUsers.take(6)
            }
            // Standard slot population logic for non-conformant remaining ones
        }
    }

    val bgRes = baseProvider?.bgResId ?: R.drawable.bg_obsidian_glass
    views.setInt(R.id.widget_root, "setBackgroundResource", bgRes)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun getAvatarBitmap(context: Context, user: UserInfo, scaleFactor: Float = 0.6f): android.graphics.Bitmap {
    val size = 120 // Larger canvas for better quality
    val contentSize = (size * scaleFactor)
    val avatarPath = ShellUtils.getUserAvatarPath(context, user.id)
    
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

    if (avatarPath != null) {
        try {
            val original = android.graphics.BitmapFactory.decodeFile(avatarPath)
            if (original != null) {
                val scaled = android.graphics.Bitmap.createScaledBitmap(original, contentSize.toInt(), contentSize.toInt(), true)
                val offset = (size - contentSize) / 2f
                canvas.drawBitmap(scaled, offset, offset, paint)
                return bitmap
            }
        } catch (e: Exception) {}
    }
    
    // Fallback: draw initials
    val colors = listOf(
        "#E91E63", "#9C27B0", "#3F51B5", "#00BCD4", "#4CAF50", "#FF9800", "#FF5722"
    ).map { android.graphics.Color.parseColor(it) }
    
    val idx = Math.abs(user.id) % colors.size
    paint.color = colors[idx]
    canvas.drawCircle(size / 2f, size / 2f, contentSize / 2f, paint)
    
    paint.color = android.graphics.Color.WHITE
    paint.textSize = contentSize / 2f
    paint.textAlign = android.graphics.Paint.Align.CENTER
    val letter = if (user.name.isNotEmpty()) user.name.substring(0, 1).uppercase() else "?"
    val textY = (size / 2f) - ((paint.descent() + paint.ascent()) / 2f)
    canvas.drawText(letter, size / 2f, textY, paint)
    return bitmap
}

private fun updateUserItem(
    context: Context,
    views: RemoteViews,
    slot: WidgetSlot,
    user: UserInfo,
    currentUser: Int,
    appWidgetId: Int,
    providerClass: Class<*>,
    isMainInGrid: Boolean = false,
    isQuantum: Boolean = false,
    scaleFactor: Float = 0.6f
) {
    views.setImageViewBitmap(slot.iconId, getAvatarBitmap(context, user, scaleFactor))
    
    // Remove oval circle
    views.setInt(slot.circleId, "setBackgroundResource", 0)
    
    // All names visible, only active is neon green
    val isActive = user.id == currentUser
    views.setViewVisibility(slot.nameId, android.view.View.VISIBLE)
    views.setTextViewText(slot.nameId, user.name)
    
    if (isActive) {
        views.setTextColor(slot.nameId, android.graphics.Color.parseColor("#00FF00"))
    } else {
        views.setTextColor(slot.nameId, android.graphics.Color.parseColor("#FFFFFF"))
    }

    val intent = Intent(context, providerClass).apply {
        action = MultiuserWidgetProvider.ACTION_SWITCH_USER
        putExtra(MultiuserWidgetProvider.EXTRA_USER_ID, user.id)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, appWidgetId + user.id, intent, 
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(slot.rootId, pendingIntent)
}

