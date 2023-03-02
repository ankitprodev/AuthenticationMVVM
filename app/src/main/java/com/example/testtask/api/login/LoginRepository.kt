package com.example.testtask.api.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.testtask.api.authentication.model.User
import com.example.testtask.utils.Constants
import com.example.testtask.utils.HelperClass
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class LoginRepository(private val context: Context) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()

    // This function is used to same sign-in with google details on firebase
    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<User>? {
        val authenticatedUserMutableLiveData: MutableLiveData<User> = MutableLiveData<User>()
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
                    authenticatedUserMutableLiveData.setValue(user)
                }
            } else {
                HelperClass.logErrorMessage(context, authTask.getException()?.message)
            }
        }
        return authenticatedUserMutableLiveData
    }

    // This function is used to sign-in using email and password
    fun firebaseSignIn(email: String, password: String): MutableLiveData<User>? {
        val authenticatedUserMutableLiveData: MutableLiveData<User> = MutableLiveData<User>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
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
                        authenticatedUserMutableLiveData.setValue(user)
                    }
                } else {
                    HelperClass.logErrorMessage(context, authTask.getException()?.message)
                }

            }

        return authenticatedUserMutableLiveData
    }
}