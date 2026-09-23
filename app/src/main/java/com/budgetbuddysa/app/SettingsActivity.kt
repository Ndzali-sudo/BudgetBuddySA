package com.budgetbuddysa.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val notificationsSwitch =
            findViewById<Switch>(R.id.notificationsSwitch)

        val logoutButton =
            findViewById<Button>(R.id.logoutBtn)

        val aboutButton =
            findViewById<Button>(R.id.aboutBtn)

        val preferences =
            getSharedPreferences(
                "BudgetBuddySettings",
                MODE_PRIVATE
            )

        notificationsSwitch.isChecked =
            preferences.getBoolean(
                "notificationsEnabled",
                true
            )

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->

            preferences.edit()
                .putBoolean(
                    "notificationsEnabled",
                    isChecked
                )
                .apply()

            Toast.makeText(
                this,
                if (isChecked) {
                    "Notifications enabled"
                } else {
                    "Notifications disabled"
                },
                Toast.LENGTH_SHORT
            ).show()
        }

        aboutButton.setOnClickListener {

            Toast.makeText(
                this,
                "BudgetBuddy SA\nPersonal Budget Management App",
                Toast.LENGTH_LONG
            ).show()
        }

        logoutButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }
}