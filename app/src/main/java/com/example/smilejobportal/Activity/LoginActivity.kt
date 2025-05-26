package com.example.smilejobportal.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smilejobportal.AdminPanel.AdminAddJobDataActivity
import com.example.smilejobportal.R
import com.example.smilejobportal.ViewModel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    // Google Sign-In result launcher
    private val googleSignInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ EARLY REDIRECT: If user is already logged in, go to MainActivity and skip showing login screen
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0) // optional: no animation
            finish()
            return
        }

        // Show login screen
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val forgotPassword = findViewById<TextView>(R.id.forgotPasswordText)
        val signUp = findViewById<TextView>(R.id.signUpBtn)
        val rememberMe = findViewById<CheckBox>(R.id.rememberMeCheckBox)
        val googleSignInBtn = findViewById<ImageView>(R.id.googleSignInBtn)

        // SharedPreferences for Remember Me
        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", "")
        val savedPassword = sharedPref.getString("password", "")

        // Autofill saved credentials
        email.setText(savedEmail)
        password.setText(savedPassword)
        rememberMe.isChecked = savedEmail!!.isNotEmpty()

        // Email/password login
        loginBtn.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Shortcut for admin access
            if (emailText == "admin" && passwordText == "admin123") {
                startActivity(Intent(this, AdminAddJobDataActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // Normal user login
            viewModel.login(emailText, passwordText) { success, message ->
                if (success) {
                    if (rememberMe.isChecked) {
                        sharedPref.edit().apply {
                            putString("email", emailText)
                            putString("password", passwordText)
                            apply()
                        }
                    } else {
                        sharedPref.edit().clear().apply()
                    }

                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                    // Save FCM token to database
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        FirebaseAuth.getInstance().uid?.let { uid ->
                            FirebaseDatabase.getInstance().getReference("users")
                                .child(uid)
                                .child("fcmToken")
                                .setValue(token)
                        }
                    }

                } else {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Google Sign-In setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInResult.launch(signInIntent)
        }

        // Sign-up and Forgot Password
        signUp.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        forgotPassword.setOnClickListener {
            val emailText = email.text.toString()
            if (emailText.isEmpty()) {
                Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.forgotPassword(emailText) { success, message ->
                    val msg = if (success) "Reset link sent to email" else message
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
