package com.example.testtask.ui.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.testtask.R
import com.example.testtask.databinding.ActivityRegisterBinding
import com.example.testtask.ui.main.HomeActivity
import com.example.testtask.ui.register.viewmodel.RegisterViewModel
import com.example.testtask.utils.Constants
import com.example.testtask.utils.HelperClass
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class RegisterActivity : AppCompatActivity() {

    private var registerViewModel: RegisterViewModel? = null
    private var googleSignInClient: GoogleSignInClient? = null

    private lateinit var binding: ActivityRegisterBinding

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.createAppCompatTextView.setOnClickListener {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        initSignInButton()
        initAuthViewModel()
        initGoogleSignInClient()
    }

    // Open google sign-up account dialog for trigger login flow
    private fun signUp() {
        val signUpIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signUpIntent, Constants.RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val googleSignInAccount = task.getResult(
                    ApiException::class.java
                )
                googleSignInAccount?.let { getGoogleAuthCredential(it) }
            } catch (e: ApiException) {
                HelperClass.logErrorMessage(this, e.message)
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
        registerViewModel?.signInWithGoogle(googleAuthCredential)
        registerViewModel?.authenticatedUserLiveData?.observe(this) { authenticatedUser ->
            goToMainActivity()
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }

    // Click event of buttons
    private fun initSignInButton() {
        // Google sign-up button click
        binding.googleSignUpButton.setOnClickListener { v: View? -> signUp() }

        // Register button click event for field validation and signup method call
        binding.registerButton.setOnClickListener {
            if (binding.etEmail.text.toString().isNullOrBlank()) {
                Toast.makeText(
                    this@RegisterActivity,
                    resources.getString(R.string.error_email),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.etPwd.text.toString().isNullOrBlank()) {
                Toast.makeText(
                    this@RegisterActivity,
                    resources.getString(R.string.error_pwd),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                registerViewModel?.signUp(
                    binding.etEmail.text.toString().trim(),
                    binding.etPwd.text.toString().trim()
                )
                HelperClass.dismissKeyboard(this)
                registerViewModel?.authenticatedUserLiveData?.observe(this) { authenticatedUser ->

                    binding.etEmail.text.clear()
                    binding.etName.text.clear()
                    binding.etPwd.text.clear()
                    if (authenticatedUser.isNew) {
                        toastMessage(binding.etName.text.toString())
                    } else {
                        goToMainActivity()
                    }
                }
            }
        }
    }

    // Init register viewmodel
    private fun initAuthViewModel() {
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
    }

    // Show message to user after create new account
    private fun toastMessage(name: String?) {
        Toast.makeText(this, "Hi $name!\nYour account was successfully created.", Toast.LENGTH_LONG)
            .show()
    }

    // Navigate to main activity
    private fun goToMainActivity() {
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
        startActivity(HomeActivity.getIntent(this))
    }
}