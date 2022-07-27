package com.ema.cooknation

import android.content.Context

object RegisterValidator {

    /**
     * ... email regex gets checked by firebase -> no logic needed
     * ... email cannot be empty
     * ... password must have at least 8 digits
     * ... password must have at least 1 letter
     * ... password must have at least 1 number
     * ... username must have at least 3 digits
     * ... username must be unique
     */

    fun validateInput(context: Context?, email: String, username: String, password: String) {
        var retVal = true
        if (email.isEmpty()) {
            retVal = false
        }
        if (password.length < 8) {
            retVal = false
        }
        //if (password.contains())
    }
}