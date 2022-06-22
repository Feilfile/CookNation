package com.ema.cooknation.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ema.cooknation.M1Home
import com.ema.cooknation.M2Explore

class AppViewModel : ViewModel() {

    private var _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private var _currentFragment = MutableLiveData<Fragment>(M1Home())
    val currentFragment: LiveData<Fragment> = _currentFragment

    fun saveUsername(username: String) {
        _username.value = username
    }

    fun updateFragment(fragment: Fragment) {
        _currentFragment.value = fragment
    }

    fun getFragment(): LiveData<Fragment> {
        return currentFragment
    }



}