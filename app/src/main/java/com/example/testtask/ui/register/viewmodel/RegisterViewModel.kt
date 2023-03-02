package com.example.testtask.ui.register.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.testtask.api.authentication.model.User
import com.example.testtask.api.register.RegisterRepository
import com.google.firebase.auth.AuthCredential

class RegisterViewModel(application: Application?) : AndroidViewModel(application!!) {
    private val registerRepository: RegisterRepository
    var authenticatedUserLiveData: LiveData<User>? = null

    // Init repository
    init {
        registerRepository = RegisterRepository(application?.applicationContext!!)
    }


    // Signup with google on firebase
    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        authenticatedUserLiveData = registerRepository.firebaseSignUpWithGoogle(googleAuthCredential)
    }

    // Sign up using email on firebase
    fun signUp(email: String, password: String) {
        authenticatedUserLiveData = registerRepository.register(email, password)
    }

}