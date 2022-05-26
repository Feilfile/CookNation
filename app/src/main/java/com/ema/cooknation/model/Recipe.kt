package com.ema.cooknation.model


data class Recipe (
    val name: String,
    val directions: String)
{
    var ratingAvg: Number=0
    var ratingCount: Number=0
    var ingredients: String=""
}