package com.ema.cooknation.model

import java.io.Serializable
import java.sql.Date
import java.sql.Timestamp

/*var recipeList = mutableListOf<Recipe>()

class Recipe {

    val FIELD_TITLE = "title"
    val FIELD_DATE = "date"
    val FIELD_PICTURE_PATH = "picturePath"
    val FIELD_DIRECTIONS = "directions"
    val FIELD_INGREDIANTS = "ingredients"
    val FIELD_AVG_RATING = "avgRating"

    private var title: String? = null
    private var date: String? = null
    private var picturePath: String? = null
    private var directions: String? = null
    private var ingredients: String? = null
    private var numRatings: Int? = null
    private var avgRating: Double? = null

    constructor() {}

    constructor(
        title: String?, date: String?, picturePath: String?, directions: String?,
        ingredients: String?, numRatings: Int, avgRating: Double
    ) {
        this.title = title
        this.date = date
        this.picturePath = picturePath
        this.directions = directions
        this.ingredients = ingredients
        this.numRatings = numRatings
        this.avgRating = avgRating
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

    fun getPicturePath(): String? {
        return picturePath
    }

    fun setPicturePath(PicturePath: String?) {
        this.picturePath = picturePath
    }

    fun getDirections(): String? {
        return directions
    }

    fun setDirections(photo: String?) {
        this.directions = directions
    }

    fun getIngredients(): String? {
        return ingredients
    }

    fun setIngredients(price: Int) {
        this.ingredients = ingredients
    }

    fun getNumRatings(): Int? {
        return numRatings
    }

    fun setNumRatings(numRatings: Int) {
        this.numRatings = numRatings
    }

    fun getAvgRating(): Double? {
        return avgRating
    }

    fun setAvgRating(avgRating: Double) {
        this.avgRating = avgRating
    }


}*/

data class Recipe (
    var title: String? = null,
    var author: String? = null,
    var date: java.util.Date? = null,
    var picturePath: String? = null,
    var directions: String? = null,
    var ingredients: String? = null,
    var numRatings: Int = 0,
    var avgRating: Double = 0.0) : Serializable {
    }
