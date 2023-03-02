package com.example.testtask.ui.login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.testtask.api.authentication.model.User
import com.example.testtask.api.login.LoginRepository
import com.google.firebase.auth.AuthCredential

class LoginViewModel(application: Application?) : AndroidViewModel(application!!) {
    private val loginRepository: LoginRepository

    var authenticatedUserLiveData: LiveData<User>? = null
    var createdUserLiveData: LiveData<User>? = null

    // Init repository
    init {
        loginRepository = LoginRepository(application?.applicationContext!!)
    }

    // Signin with google on firebase
    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        authenticatedUserLiveData = loginRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    // Sign in using email on firebase
    fun signUp(email: String, password: String) {
        authenticatedUserLiveData = loginRepository.firebaseSignIn(email, password)
    }

}