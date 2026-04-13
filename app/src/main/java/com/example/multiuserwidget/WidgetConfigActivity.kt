package com.example.multiuserwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class WidgetConfigActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        setResult(RESULT_CANCELED)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val users = ShellUtils.listUsers()
        val listView = findViewById<ListView>(R.id.user_list)
        val info = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId)
        val className = info.provider.className
        val isHub = className.contains("Ribbon") || 
                    className.contains("Hub") || 
                    className.contains("Neon") || 
                    className.contains("Quantum") || 
                    className.contains("Expand")

        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, users.map { "${it.name} (ID: ${it.id})" })
        listView.adapter = adapter

        findViewById<MaterialButton>(R.id.select_all_button).setOnClickListener {
            for (i in 0 until adapter.count) {
                listView.setItemChecked(i, true)
            }
        }

        findViewById<MaterialButton>(R.id.save_button).setOnClickListener {
            val selectedIds = mutableListOf<Int>()
            val sparsePositions = listView.checkedItemPositions
            for (i in 0 until sparsePositions.size()) {
                if (sparsePositions.valueAt(i)) {
                    selectedIds.add(users[sparsePositions.keyAt(i)].id)
                }
            }
            saveHubPrefs(selectedIds)
            if (selectedIds.isNotEmpty()) {
                saveWidgetPref(users.find { it.id == selectedIds.first() } ?: users.first())
            }
            
            completeConfig(info.provider.className)
        }
    }

    private fun completeConfig(className: String) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val providerClass = try {
            Class.forName(className).newInstance() as MultiuserWidgetProvider
        } catch (e: Exception) {
            MultiuserWidgetProvider()
        }

        updateAppWidget(this, appWidgetManager, appWidgetId, providerClass)

        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    private fun saveHubPrefs(ids: List<Int>) {
        val prefs = getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE).edit()
        prefs.putString("hub_$appWidgetId", ids.joinToString(","))
        prefs.apply()
    }

    private fun saveWidgetPref(user: UserInfo) {
        val prefs = getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE).edit()
        prefs.putInt("widget_$appWidgetId", user.id)
        prefs.putString("name_$appWidgetId", user.name)
        prefs.apply()
    }
}
