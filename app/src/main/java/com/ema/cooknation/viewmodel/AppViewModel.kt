package com.ema.cooknation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {

    /*private var _currentRecipeTitle= MutableLiveData<String>()
    val currentRecipeTitle: LiveData<String> =  _currentRecipeTitle

    fun saveCurrentRecipeName(title: String) {
        _currentRecipeTitle.value = title
    }

    fun getCurrentRecipeName(): LiveData<String> {
        return currentRecipeTitle

        */
    val currentRecipeTitle: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}