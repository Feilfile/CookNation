package com.ema.cooknation

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import org.w3c.dom.Text

object RecipeInputValidator {

    /**
     * ... title has to have at least 5 digits
     * ... directions must have at least 30 digits
     * ... ingredients must have at least 30 digits
     * ... a picture has to be initialized
     */

    fun validateInputs(title: String, directions: String, ingredients: String, pictureInitialized: Boolean ): Boolean {
        if( title.count {it.isDigit()} <= 5) {
            return false
        }
        if (directions.count{it.isDigit()} <= 30) {
            return false
        }
        if (ingredients.count{it.isDigit()} <= 30) {
            return false
        }
        if (!pictureInitialized) {
            return false}
        return true
    }

    /*private fun errorMessages(errorMessage: CharSequence) {
        Toast.makeText(
            this,
            errorMessage,
            Toast.LENGTH_SHORT
        ).show()

        Toast.makeText(Context, "errorMessage", 3)


    }*/
}