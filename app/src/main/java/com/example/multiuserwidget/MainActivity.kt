package com.example.multiuserwidget

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: UserListAdapter
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.status_text)
        val loadButton = findViewById<MaterialButton>(R.id.load_users_button)
        val deployButton = findViewById<MaterialButton>(R.id.deploy_button)
        val testButton = findViewById<MaterialButton>(R.id.test_button)
        val exitButton = findViewById<MaterialButton>(R.id.exit_button)
        val userList = findViewById<RecyclerView>(R.id.user_manage_list)

        // Initialize User List
        adapter = UserListAdapter(this, emptyList())
        userList.layoutManager = LinearLayoutManager(this)
        userList.adapter = adapter

        // Root Check at Startup (Plan v6 optimization)
        if (ShellUtils.checkRootAccess()) {
            Toast.makeText(this, "Root Access Granted", Toast.LENGTH_SHORT).show()
        } else {
            statusText.text = "ROOT REQUIRED!"
            Toast.makeText(this, "Root Access Denied", Toast.LENGTH_LONG).show()
        }

        loadButton.setOnClickListener {
            val users = ShellUtils.listUsers()
            if (users.isNotEmpty()) {
                adapter.updateUsers(users)
                statusText.text = "Gefundene Benutzer: ${users.size}"
            } else {
                Toast.makeText(this, "Keine Benutzer gefunden", Toast.LENGTH_SHORT).show()
            }
        }

        deployButton.setOnClickListener {
            val results = ShellUtils.deployToAllUsers()
            Toast.makeText(this, "Deployment abgeschlossen", Toast.LENGTH_SHORT).show()
        }

        testButton.setOnClickListener {
            val result = ShellUtils.executeCommand("id")
            Toast.makeText(this, "Result: $result", Toast.LENGTH_LONG).show()
        }

        exitButton.setOnClickListener {
            finishAffinity()
            exitProcess(0)
        }
    }
}
