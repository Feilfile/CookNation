package com.ema.cooknation.data

import androidx.lifecycle.LiveData

class LocalRecipeRepository(private val localRecipeDao: LocalRecipeDao) {

    val readAllData: LiveData<List<LocalRecipe>> = localRecipeDao.readAllData()

    suspend fun addLocalRecipe(localRecipe: LocalRecipe){
        localRecipeDao.addRecipe(localRecipe)
    }

    suspend fun deleteRecipe(localRecipe: LocalRecipe){
        localRecipeDao.deleteRecipe(localRecipe)
    }

    suspend fun deleteLocalRecipe(docId: String){
        localRecipeDao.deleteLocalRecipe(docId)
    }

    suspend fun getAllRecipesAtOnce(): List<LocalRecipe>{
        return localRecipeDao.getAllRecipesAtOnce()
    }

    fun selectDocumentByDocID(docId: String): LiveData<LocalRecipe>{
        return localRecipeDao.selectDocumentByDocID(docId)
    }
}