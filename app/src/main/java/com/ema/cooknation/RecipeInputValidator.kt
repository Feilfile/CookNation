package com.ema.cooknation

import android.content.Context
import android.widget.Toast

object RecipeInputValidator {

    /**
     * ... title has to have at least 5 digits
     * ... directions must have at least 30 digits
     * ... ingredients must have at least 30 digits
     * ... a picture has to be initialized
     */

    fun validateInputs(context: Context?, title: String, directions: String, ingredients: String, pictureInitialized: Boolean ): Boolean {
        var retVal = true
        if (title.length < 5) {
            errorMessages("Title must have at least 5 digits", context)
            retVal = false
        }
        if (ingredients.length < 30) {
            errorMessages("Ingredients must have at least 30 digits", context)
            retVal = false
        }
        if (directions.length < 30) {
            errorMessages("Directions must have at least 30 digits", context)
            retVal = false
        }
        if (!pictureInitialized) {
            errorMessages("A picture needs to be selected", context)
            retVal = false}
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