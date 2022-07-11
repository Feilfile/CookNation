package com.ema.cooknation.model

import java.io.Serializable
import java.util.*

/*var recipeList = mutableListOf<Recipe>()

class Recipe {

    private var author: String? = null
    private var title: String? = null
    private var date: Date? = null
    private var picturePath: String? = null
    private var directions: String? = null
    private var ingredients: String? = null
    private var ratingCount: Int? = null
    private var avgRating: Double? = null
    private var uid : String? = null

    constructor(
        author : String, avgRating: Double, date: Date, directions: String,
        ingredients: String, picturePath: String, ratingCount: Int, title: String, uid: String
    ) {
        this.author = author
        this.title = title
        this.date = date
        this.picturePath = picturePath
        this.directions = directions
        this.ingredients = ingredients
        this.ratingCount = ratingCount
        this.avgRating = avgRating
        this.uid = uid
    }

    fun getTitle(): String? {
        return title
    }

    fun getAuthor(): String? {
        return author
    }

    fun getIngredients(): String? {
        return ingredients
    }

    fun getDirections(): String? {
        return directions
    }

    fun getDate(): Date? {
        return date
    }

    fun getRatingCount(): Int? {
        return ratingCount
    }

    fun getAvgRating(): Double? {
        return avgRating
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setDate(date: Date?) {
        this.date = date
    }

    fun getPicturePath(): String? {
        return picturePath
    }

    fun setPicturePath(PicturePath: String?) {
        this.picturePath = picturePath
    }

    fun setIngredients(price: Int) {
        this.ingredients = ingredients
    }

    fun setAvgRating(avgRating: Double) {
        this.avgRating = avgRating
    }

}*/

data class Recipe(
    var uid: String? = null,
    var title: String? = null,
    var author: String? = null,
    var date: Date? = null,
    var picturePath: String? = null,
    var directions: String? = null,
    var ingredients: String? = null,
    var ratingCount: Int = 0,
    var avgRating: Float = 0.0f) : Serializable {

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
