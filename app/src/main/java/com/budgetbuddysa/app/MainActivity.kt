package com.budgetbuddysa.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        requestNotificationPermission()
        generateFirebaseToken()

        val registerButton = findViewById<Button>(R.id.registerBtn)

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val loginButton = findViewById<Button>(R.id.loginBtn)

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }

    private fun generateFirebaseToken() {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val token = task.result

                    Log.d("BudgetBuddyFCM", "FCM TOKEN: $token")

                    Toast.makeText(
                        this,
                        "Firebase notifications are ready!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Log.e(
                        "BudgetBuddyFCM",
                        "Token generation failed",
                        task.exception
                    )
                }
            }
    }



}
