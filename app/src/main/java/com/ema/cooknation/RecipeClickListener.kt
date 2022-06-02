package com.ema.cooknation

import com.ema.cooknation.model.Recipe

interface RecipeClickListener {
    fun onClick(recipe: Recipe)
}