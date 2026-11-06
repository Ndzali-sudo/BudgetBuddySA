package com.budgetbuddysa.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        val emailInput = findViewById<EditText>(R.id.loginEmailInput)
        val passwordInput = findViewById<EditText>(R.id.loginPasswordInput)
        val loginButton = findViewById<Button>(R.id.loginAccountBtn)
        val googleButton = findViewById<Button>(R.id.googleSignInBtn)
        val registerText = findViewById<TextView>(R.id.goToRegisterText)

        loginButton.setOnClickListener {

            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showMessage("Please enter your email and password")
            } else if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email).matches()
            ) {
                showMessage("Please enter a valid email address")
            } else {
                loginWithEmail(email, password)
            }
        }

        googleButton.setOnClickListener {
            signInWithGoogle()
        }

        registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginWithEmail(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    showMessage("Login successful!")

                    startActivity(
                        Intent(this, DashboardActivity::class.java)
                    )

                    finish()

                } else {

                    showMessage(
                        task.exception?.localizedMessage
                            ?: "Login failed. Check your details."
                    )
                }
            }
    }

    private fun signInWithGoogle() {

        lifecycleScope.launch {

            try {

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        getString(R.string.default_web_client_id)
                    )
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )

                handleGoogleCredential(result.credential)

            } catch (e: GetCredentialException) {

                showMessage("Google sign-in failed: ${e.message}")

            } catch (e: Exception) {

                showMessage("Sign-in error: ${e.message}")
            }
        }
    }

    private fun handleGoogleCredential(credential: Credential) {

        if (
            credential is CustomCredential &&
            credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            try {

                val googleCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                val firebaseCredential = GoogleAuthProvider.getCredential(
                    googleCredential.idToken,
                    null
                )

                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {

                            showMessage("Google login successful!")

                            startActivity(
                                Intent(this, DashboardActivity::class.java)
                            )

                            finish()

                        } else {

                            showMessage(
                                task.exception?.localizedMessage
                                    ?: "Google login failed"
                            )
                        }
                    }

            } catch (e: Exception) {

                showMessage("Could not process Google account: ${e.message}")
            }

        } else {

            showMessage("Invalid Google sign-in credential.")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}