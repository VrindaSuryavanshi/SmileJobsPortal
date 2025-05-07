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
    private val RC_SIGN_IN = 9001

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
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val forgotPassword = findViewById<TextView>(R.id.forgotPasswordText)
        val signUp = findViewById<TextView>(R.id.signUpBtn)
        val rememberMe = findViewById<CheckBox>(R.id.rememberMeCheckBox)

        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", "")
        val savedPassword = sharedPref.getString("password", "")

        // Autofill if data saved
        email.setText(savedEmail)
        password.setText(savedPassword)
        rememberMe.isChecked = savedEmail!!.isNotEmpty()

        loginBtn.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the login is for the admin account
            if (emailText == "admin" && passwordText == "admin123") {
                startActivity(Intent(this, AdminAddJobDataActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // Normal login
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

                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        FirebaseAuth.getInstance().uid?.let { uid ->
                            FirebaseDatabase.getInstance().getReference("users")
                                .child(uid)
                                .child("fcmToken")
                                .setValue(token)
                        }
                    }



                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Set up Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<ImageView>(R.id.googleSignInBtn).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInResult.launch(signInIntent)
        }

        signUp.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        forgotPassword.setOnClickListener {
            viewModel.forgotPassword(email.text.toString()) { success, message ->
                val msg = if (success) "Reset link sent to email" else message
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
