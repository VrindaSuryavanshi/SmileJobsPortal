package com.example.smilejobportal.ViewModel

import androidx.lifecycle.ViewModel
import com.example.smilejobportal.Repository.AuthRepository


// ui/register/RegistrationViewModel.kt
class RegistrationViewModel : ViewModel() {
    private val repo = AuthRepository()

    fun register(name: String, email: String, contact: String, password: String,cPassword: String, callback: (Boolean, String?) -> Unit) {
        repo.registerUser(email, password,cPassword, name, contact, callback)
    }
}
