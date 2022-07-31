package com.ema.cooknation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ema.cooknation.R

/**
 * Activity opens in case you don't have a internet connection
 * */

class OfflineScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.offline_screen, container, false)
    }

    companion object {
        fun newInstance() = OfflineScreen().apply {}
    }
}