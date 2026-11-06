package com.budgetbuddysa.app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput =
            findViewById<EditText>(R.id.confirmPasswordInput)

        val registerButton =
            findViewById<Button>(R.id.registerAccountBtn)

        registerButton.setOnClickListener {

            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            when {
                name.isEmpty() || email.isEmpty() ||
                        password.isEmpty() || confirmPassword.isEmpty() -> {
                    showMessage("Please fill in all fields")
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showMessage("Please enter a valid email address")
                }

                password.length < 6 -> {
                    showMessage("Password must contain at least 6 characters")
                }

                password != confirmPassword -> {
                    showMessage("Passwords do not match")
                }

                else -> {
                    registerUser(name, email, password)
                }
            }
        }
    }

    private fun registerUser(
        name: String,
        email: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener {

                            showMessage("Account created successfully!")

                            val intent = Intent(
                                this,
                                DashboardActivity::class.java
                            )

                            startActivity(intent)
                            finish()
                        }

                } else {
                    showMessage(
                        task.exception?.localizedMessage
                            ?: "Registration failed. Please try again."
                    )
                }
            }
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}