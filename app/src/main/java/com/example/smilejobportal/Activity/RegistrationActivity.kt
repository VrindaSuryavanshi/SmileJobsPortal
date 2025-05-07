package com.example.smilejobportal.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smilejobportal.R
import com.example.smilejobportal.ViewModel.RegistrationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class RegistrationActivity : AppCompatActivity() {
    private lateinit var viewModel: RegistrationViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        val name = findViewById<EditText>(R.id.nameEditText)
        val email = findViewById<EditText>(R.id.emailEditText)
        val contact = findViewById<EditText>(R.id.contactEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val cPassword = findViewById<EditText>(R.id.cPasswordEditText)
        val continueBtn = findViewById<Button>(R.id.continueButton)
        val loginTextView = findViewById<LinearLayout>(R.id.go_login_screen)
        val backArrow = findViewById<ImageView>(R.id.back_arrow)

        // Google Sign-In Configuration
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()



        loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        backArrow.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        continueBtn.setOnClickListener {
            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val contactText = contact.text.toString().trim()
            val passwordText = password.text.toString()
            val cPasswordText = cPassword.text.toString()

            // Validation
            when {
                nameText.isEmpty() -> {
                    name.error = "Name is required"
                    return@setOnClickListener
                }
                emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches() -> {
                    email.error = "Enter a valid email address"
                    return@setOnClickListener
                }
                contactText.length != 10 || !contactText.all { it.isDigit() } -> {
                    contact.error = "Enter a valid 10-digit contact number"
                    return@setOnClickListener
                }
                passwordText.length < 6 -> {
                    password.error = "Password must be at least 6 characters"
                    return@setOnClickListener
                }
                passwordText != cPasswordText -> {
                    cPassword.error = "Passwords do not match"
                    return@setOnClickListener
                }
                else -> {
                    viewModel.register(nameText, emailText, contactText, passwordText, cPasswordText) { success, message ->
                        if (success) {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Firebase auth failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
