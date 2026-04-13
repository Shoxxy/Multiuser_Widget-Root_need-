package com.example.multiuserwidget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

class UserListAdapter(
    private val context: Context,
    private var users: List<UserInfo>
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.user_name)
        val userSwitch: SwitchMaterial = view.findViewById(R.id.user_switch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_list_manage, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userName.text = "${user.name} (ID: ${user.id})"
        
        // Get current global toggle state
        val enabledUsers = prefs.getStringSet("EnabledUserIds", setOf()) ?: setOf()
        holder.userSwitch.isChecked = enabledUsers.contains(user.id.toString())

        holder.userSwitch.setOnCheckedChangeListener { _, isChecked ->
            val currentSet = prefs.getStringSet("EnabledUserIds", setOf())?.toMutableSet() ?: mutableSetOf()
            if (isChecked) {
                currentSet.add(user.id.toString())
            } else {
                currentSet.remove(user.id.toString())
            }
            prefs.edit().putStringSet("EnabledUserIds", currentSet).apply()

            // GLOBAL SYNC (Plan v7 Requirement)
            ShellUtils.saveGlobalConfig(currentSet)

            // CRITICAL: Notify all widgets that data has changed
            val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(context)
            
            // Notify widgets to update through standard broadcast since Hub/Ribbon were deleted
            val intent = android.content.Intent(context, MultiuserWidgetProvider::class.java).apply {
                action = android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            context.sendBroadcast(intent)
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<UserInfo>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
