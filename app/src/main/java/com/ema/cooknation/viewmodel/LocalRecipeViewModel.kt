package com.ema.cooknation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ema.cooknation.data.LocalRecipe
import com.ema.cooknation.data.LocalRecipeDatabase
import com.ema.cooknation.data.LocalRecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalRecipeViewModel(application: Application): AndroidViewModel(application) {

    private val readAllData: LiveData<List<LocalRecipe>>
    private val repository: LocalRecipeRepository

    init {
        val localRecipeDao = LocalRecipeDatabase.getDatabase(application).localRecipeDao()
        repository = LocalRecipeRepository(localRecipeDao)
        readAllData = repository.readAllData
    }

    fun addLocalRecipe(localRecipe: LocalRecipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLocalRecipe(localRecipe)
        }
    }
}