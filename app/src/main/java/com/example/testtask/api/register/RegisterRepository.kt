package com.example.testtask.api.register

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.testtask.api.authentication.model.User
import com.example.testtask.utils.Constants
import com.example.testtask.utils.HelperClass
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterRepository(private val context: Context) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Firebase signup with google setup for success and error handling
    fun firebaseSignUpWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<User>? {
        val signupUserMutableLiveData: MutableLiveData<User> = MutableLiveData<User>()
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful()) {
                val isNewUser: Boolean =
                    authTask.getResult().getAdditionalUserInfo()?.isNewUser() ?: false
                val firebaseUser = firebaseAuth.getCurrentUser()
                if (firebaseUser != null) {
                    val uid: String = firebaseUser.getUid()
                    val name = firebaseUser.getDisplayName()
                    val email = firebaseUser.getEmail()
                    val user = User(uid, name, email)
                    user.isNew = isNewUser
                    signupUserMutableLiveData.setValue(user)
                }
            } else {
                HelperClass.logErrorMessage(context, authTask.getException()?.message)
            }
        }
        return signupUserMutableLiveData
    }

    // Firebase signup with email setup for success and error handling
    fun register(email: String, password: String): MutableLiveData<User>? {
        val signupUserMutableLiveData: MutableLiveData<User> = MutableLiveData<User>()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful()) {
                    val isNewUser: Boolean =
                        authTask.getResult().getAdditionalUserInfo()?.isNewUser() ?: false
                    val firebaseUser = firebaseAuth.getCurrentUser()
                    if (firebaseUser != null) {
                        val uid: String = firebaseUser.getUid()
                        val name = firebaseUser.getDisplayName()
                        val email = firebaseUser.getEmail()
                        val user = User(uid, name, email)
                        user.isNew = isNewUser
                        signupUserMutableLiveData.setValue(user)
                    }
                } else {
                    println("Add user")
                    HelperClass.logErrorMessage(context, authTask.getException()?.message)
                }

            }

        return signupUserMutableLiveData
    }
}