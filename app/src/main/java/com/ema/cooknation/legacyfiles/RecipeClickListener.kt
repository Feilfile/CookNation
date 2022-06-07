package com.ema.cooknation.legacyfiles

import com.ema.cooknation.model.Recipe

interface RecipeClickListener {
    fun onClick(recipe: Recipe)
}