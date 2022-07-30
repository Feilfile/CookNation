package com.ema.cooknation.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_table")
data class LocalRecipe(
    @PrimaryKey(autoGenerate = false)
    val docId: String,
    var uid: String,
    var title: String,
    var author: String,
    var date: Long,
    var picture: ByteArray,
    var directions: String,
    var ingredients: String,
    var ratingCount: Int,
    var avgRating: Float,
    var prepTime: String,
    var difficulty: String
)