package com.budgetbuddysa.app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var languageSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val notificationsSwitch =
            findViewById<Switch>(R.id.notificationsSwitch)

        val logoutButton =
            findViewById<Button>(R.id.logoutBtn)

        val aboutButton =
            findViewById<Button>(R.id.aboutBtn)

        languageSpinner =
            findViewById(R.id.languageSpinner)

        // Language options
        val languages = arrayOf(
            getString(R.string.english_language),
            getString(R.string.afrikaans_language)
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languages
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        languageSpinner.adapter = adapter

        // Load notification preference
        val preferences = getSharedPreferences(
            "BudgetBuddySettings",
            MODE_PRIVATE
        )

        notificationsSwitch.isChecked =
            preferences.getBoolean("notificationsEnabled", true)

        // Save notification preference
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->

            preferences.edit()
                .putBoolean("notificationsEnabled", isChecked)
                .apply()

            Toast.makeText(
                this,
                if (isChecked)
                    getString(R.string.notifications_enabled)
                else
                    getString(R.string.notifications_disabled),
                Toast.LENGTH_SHORT
            ).show()
        }

        // Load current language
        val currentLanguage =
            AppCompatDelegate.getApplicationLocales().toLanguageTags()

        languageSpinner.setSelection(
            if (currentLanguage == "af") 1 else 0
        )

        // Change language
        languageSpinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    val languageCode =
                        if (position == 1) "af" else "en"

                    val selectedLanguage =
                        AppCompatDelegate.getApplicationLocales()
                            .toLanguageTags()

                    if (selectedLanguage != languageCode) {

                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(languageCode)
                        )
                    }
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }

        // About button
        aboutButton.setOnClickListener {

            Toast.makeText(
                this,
                getString(R.string.about_message),
                Toast.LENGTH_LONG
            ).show()
        }

        // Logout button
        logoutButton.setOnClickListener {

            val intent = Intent(
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

