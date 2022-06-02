package com.ema.cooknation.model

var recipeList = mutableListOf<Recipe>()

val RECIPE_ID_EXTRA = "recipeExtra"

class Recipe (
    var recipeImg: Int,
    var recipeName: String,
    var author: String,
    var directions: String,
    //var ratingAvg: Number,
    //var ratingCount: Number,
    var ingredients: String,
    val id: Int? = recipeList.size
    )
