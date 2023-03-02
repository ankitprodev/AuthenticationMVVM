package com.example.testtask.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testtask.utils.Constants.Companion.TAG

// This class is used for common functions used at multiple places
object HelperClass {

    // Used to display error message
    fun logErrorMessage(context: Context, errorMessage: String?) {
        Log.d(TAG, errorMessage!!)
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    // Used to hide keyboard
    fun dismissKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService( Context.INPUT_METHOD_SERVICE ) as InputMethodManager
        if( inputMethodManager.isAcceptingText ) inputMethodManager.hideSoftInputFromWindow( activity.currentFocus?.windowToken, /*flags:*/ 0)
    }
}