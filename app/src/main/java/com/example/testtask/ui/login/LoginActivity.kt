package com.example.testtask.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.testtask.R
import com.example.testtask.api.authentication.model.User
import com.example.testtask.databinding.ActivityMainBinding
import com.example.testtask.ui.login.viewmodel.LoginViewModel
import com.example.testtask.ui.main.HomeActivity
import com.example.testtask.ui.register.RegisterActivity
import com.example.testtask.utils.Constants.Companion.RC_SIGN_IN
import com.example.testtask.utils.HelperClass.dismissKeyboard
import com.example.testtask.utils.HelperClass.logErrorMessage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private var loginViewModel: LoginViewModel? = null
    private var googleSignInClient: GoogleSignInClient? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Used to navigate to create new account
        binding.createAppCompatTextView.setOnClickListener {
            startActivity(RegisterActivity.getIntent(this@LoginActivity))
        }


        initSignInButton()
        initAuthViewModel()
        initGoogleSignInClient()
    }


    // Handle sign-in button and google sign in button click event
    private fun initSignInButton() {
        binding.googleSignInButton.setOnClickListener { v: View? -> signIn() }

        binding.btn.setOnClickListener {
            if(binding.etEmail.text.toString().isNullOrBlank()) {
                Toast.makeText(this@LoginActivity, resources.getString(R.string.error_email), Toast.LENGTH_SHORT).show()
            } else if(binding.etPwd.text.toString().isNullOrBlank()) {
                Toast.makeText(this@LoginActivity, resources.getString(R.string.error_pwd), Toast.LENGTH_SHORT).show()
            } else {
                loginViewModel?.signUp(binding.etEmail.text.toString().trim(), binding.etPwd.text.toString().trim())
                dismissKeyboard(this)

                loginViewModel?.authenticatedUserLiveData?.observe(this) { authenticatedUser ->
                    binding.etEmail.text.clear()
                    binding.etPwd.text.clear()
                    goToMainActivity(authenticatedUser)
                }
            }
        }
    }

    // Init login view model
    private fun initAuthViewModel() {
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    }

    // init google-signin on options (Configure google sign-in client)
    private fun initGoogleSignInClient() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        googleSignInClient?.signOut()
    }

    // Open google sign-in account dialog for trigger login flow
    private fun signIn() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val googleSignInAccount = task.getResult(
                    ApiException::class.java
                )
                googleSignInAccount?.let { getGoogleAuthCredential(it) }
            } catch (e: ApiException) {
                logErrorMessage(this, e.message)
            }
        }
    }

    // The Task returned from this call is always completed, no need to attach a listener.
    private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount) {
        val googleTokenId = googleSignInAccount.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    // Add details of login using google on firebase
    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {
        loginViewModel?.signInWithGoogle(googleAuthCredential)
        loginViewModel?.authenticatedUserLiveData?.observe(this) { authenticatedUser ->
            goToMainActivity(authenticatedUser)
        }
    }

    // Navigate to main activity
    private fun goToMainActivity(user: User) {
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()

        startActivity(HomeActivity.getIntent(this@LoginActivity))
    }
}