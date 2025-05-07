package com.example.smilejobportal.Repository

import com.example.smilejobportal.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("users")

    fun registerUser(email: String, password: String, cPassword: String, name: String, contact: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""

                if(password.equals(cPassword)) {
                    val user = User(uid, name,email,contact, password, cPassword)
                    db.child(uid).setValue(user).addOnCompleteListener {
                        onComplete(
                            it.isSuccessful,
                            if (it.isSuccessful) null else it.exception?.message
                        )
                    }
                }else{
                    onComplete(false, task.exception?.message)
                }
            } else {
                onComplete(false, task.exception?.message)
            }
        }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception?.message)
        }
    }

    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception?.message)
        }
    }
}
