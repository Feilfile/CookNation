package com.ema.cooknation.data

import java.io.Serializable
import java.util.*

data class Recipe(
    var docId: String? = null,
    var uid: String? = null,
    var title: String? = null,
    var author: String? = null,
    var date: String? = null,
    var picturePath: String? = null,
    var directions: String? = null,
    var ingredients: String? = null,
    var prepTime: String? = "-",
    var difficulty: String? = "-",
    var ratingCount: Int = 0,
    var avgRating: Float = 0.0f,
) : Serializable {

        // increments ratingCount and update avgRating with user input
        fun addNewRating(userRating: Int) {
            val tempValue = avgRating * ratingCount + userRating
            ratingCount++
            avgRating = tempValue/ratingCount
        }

        fun updateExistingRating(oldUserRating: Int, newUserRating: Int) {
            val tempValue: Float = avgRating * ratingCount - oldUserRating + newUserRating
            avgRating = tempValue/ratingCount
        }
}
