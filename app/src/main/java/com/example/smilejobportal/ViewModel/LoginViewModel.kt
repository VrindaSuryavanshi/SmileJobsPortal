package com.example.smilejobportal.ViewModel

import androidx.lifecycle.ViewModel
import com.example.smilejobportal.Repository.AuthRepository

// ui/login/LoginViewModel.kt
class LoginViewModel : ViewModel() {
    private val repo = AuthRepository()

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        repo.loginUser(email, password, callback)
    }

    fun forgotPassword(email: String, callback: (Boolean, String?) -> Unit) {
        repo.resetPassword(email, callback)
    }
}
