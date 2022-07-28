package com.ema.cooknation.legacyfiles

import com.ema.cooknation.data.Recipe

interface RecipeClickListener {
    fun onClick(recipe: Recipe)
}