package com.ema.cooknation.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.*

@Entity(tableName = "recipe_table")
data class LocalRecipe (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var uid: String,
    var title: String,
    var author: String,
    var date: Long,
    var picturePath: String,
    var directions: String,
    var ingredients: String,
    var ratingCount: Int,
    var avgRating: Float
)