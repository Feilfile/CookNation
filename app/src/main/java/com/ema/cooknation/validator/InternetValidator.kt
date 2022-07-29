package com.ema.cooknation.validator

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object InternetValidator {

    /**
     * Checks if user is online -> reused code
     * Source: https://stackoverflow.com/questions/53532406/activenetworkinfo-type-is-deprecated-in-api-level-28
     */
    fun isInternetAvailable(context: Context): Boolean {
        val result: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result
    }
}