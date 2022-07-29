package com.ema.cooknation.validator

import android.content.Context
import android.widget.Toast

object RegisterValidator {

    /**
     * ... email regex gets checked by firebase -> no logic needed
     * ... email cannot be empty
     * ... password must have at least 8 digits
     * ... password must have at least 1 letter
     * ... password must have at least 1 number
     * ... username must have at least 3 digits
     * ... username must be unique -> Firestore cant be tested in a unit Test
     */

    fun validateInputs(context: Context?, email: String, username: String, password: String): Boolean {
        var retVal = true
        if (email.isEmpty()) {
            retVal = false
            errorMessages("Email cannot be empty", context)
        }
        if (password.length < 8) {
            retVal = false
            errorMessages("Password must have at least 8 digits", context)
        }
        if (!password.contains("[a-zA-Z]".toRegex())) {
            retVal = false
            errorMessages("Password must have at least 1 letter", context)
        }
        if (!password.contains("[1234567890]".toRegex())) {
            retVal = false
            errorMessages("Password must have at least 1 number", context)
        }
        if (username.length < 3) {
            retVal = false
            errorMessages("Username must have at least 3 digits", context)
        }
        return retVal
    }

    private fun errorMessages(errorMessage: String, context: Context?) {
        if (context != null) {
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}