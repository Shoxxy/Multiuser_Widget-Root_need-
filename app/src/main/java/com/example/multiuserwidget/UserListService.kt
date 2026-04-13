package com.example.multiuserwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class UserListService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        return UserListFactory(this.applicationContext, appWidgetId)
    }
}

class UserListFactory(private val context: Context, private val appWidgetId: Int) : RemoteViewsService.RemoteViewsFactory {
    private var users = listOf<UserInfo>()
    private val colors = listOf("#D4EAE5", "#D0E2F2", "#EAD5D5", "#E1D9F0", "#E8E0D5")

    override fun onCreate() {}

    override fun onDataSetChanged() {
        val allUsers = ShellUtils.listUsers()
        val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        
        // 1. Filter by Global Toggles (Plan v7 Requirement)
        // Prioritize Global Root Config over local SharedPreferences
        val globalEnabledIds = ShellUtils.readGlobalConfig()
        val localEnabledIds = prefs.getStringSet("EnabledUserIds", setOf()) ?: setOf()
        
        val effectiveEnabledIds = if (globalEnabledIds.isNotEmpty()) globalEnabledIds else localEnabledIds
        val globallyEnabledUsers = allUsers.filter { effectiveEnabledIds.contains(it.id.toString()) }

        // 2. Further filter by Hub Selection (subset) if applicable
        val hubIdsStr = prefs.getString("hub_$appWidgetId", "") ?: ""
        if (hubIdsStr.isNotEmpty()) {
            val selectedIds = hubIdsStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
            users = globallyEnabledUsers.filter { it.id in selectedIds }
        } else {
            users = globallyEnabledUsers
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = users.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= users.size) return RemoteViews(context.packageName, R.layout.item_user_bubble)
        
        val user = users[position]
        val view = RemoteViews(context.packageName, R.layout.item_user_bubble)
        
        val initial = if (user.name.isNotEmpty()) user.name[0].toString().uppercase() else "?"
        val color = Color.parseColor(colors[position % colors.size])
        
        view.setTextViewText(R.id.user_initial, initial)
        // Background tint for the circle (View doesn't support setBackgroundColor in all API versions easily, but setInt with "setBackgroundColor" works for most)
        view.setInt(R.id.user_circle, "setBackgroundColor", color)
        
        // Only show name if list is small, otherwise it looks cluttered (Extreme Edition logic)
        if (users.size <= 4) {
             view.setViewVisibility(R.id.user_name_text, android.view.View.VISIBLE)
             view.setTextViewText(R.id.user_name_text, user.name)
        } else {
             view.setViewVisibility(R.id.user_name_text, android.view.View.GONE)
        }
        
        // FillInIntent for the switch action
        val fillInIntent = Intent().apply {
            putExtra(MultiuserWidgetProvider.EXTRA_USER_ID, user.id)
        }
        view.setOnClickFillInIntent(R.id.user_item_root, fillInIntent)
        
        return view
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
}
