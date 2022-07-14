package com.ema.cooknation.data

import androidx.lifecycle.LiveData

class LocalRecipeRepository(private val localRecipeDao: LocalRecipeDao) {

    val readAllData: LiveData<List<LocalRecipe>> = localRecipeDao.readAllData()

    suspend fun addLocalRecipe(localRecipe: LocalRecipe){
        localRecipeDao.addRecipe(localRecipe)
    }
}