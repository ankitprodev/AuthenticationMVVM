package com.example.testtask.api.authentication.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable


// User model class for pass data for sign/signup screens
data class User(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    @Exclude
    var isAuthenticated: Boolean = false,
    @Exclude
    var isNew: Boolean = false,
    @Exclude
    var isCreated: Boolean = false
) : Serializable
