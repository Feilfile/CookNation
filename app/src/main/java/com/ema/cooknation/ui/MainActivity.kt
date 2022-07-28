package com.ema.cooknation.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ema.cooknation.R
import com.ema.cooknation.viewmodel.LocalRecipeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var currentFragment: Fragment
    private lateinit var localRecipeViewModel: LocalRecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CookNation)
        setContentView(R.layout.main_activity)
        localRecipeViewModel = ViewModelProvider(this)[LocalRecipeViewModel::class.java]

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener)
        openFragment(M1Home.newInstance())
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView1, fragment)
        transaction.commit()
    }

    fun openUploadFragment() {
        startActivity(Intent(this@MainActivity, S2Upload::class.java))
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    fun performLogout() {
        val registerLogin = M4x2RegisterLogin.newInstance()
        openFragment(registerLogin)
    }

    fun openWebViewActivity() {
        startActivity(Intent(this@MainActivity, WebViewActivity::class.java))
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    fun openLoginActivity() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    fun openRegisterActivity() {
        startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val connectedToInternet = isInternetAvailable(this)
        if (!connectedToInternet && item.itemId != R.id.m1_home) {
            currentFragment = OfflineScreen.newInstance()
            openFragment(currentFragment)
            return@OnNavigationItemSelectedListener true
        }
        when (item.itemId) {
            R.id.m1_home -> {
                currentFragment = M1Home.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m2_explore -> {
                currentFragment = M2Explore.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m3_search -> {
                currentFragment = M3Search.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m4_profile -> {
                // check if user is logged in
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    currentFragment = M4x2RegisterLogin.newInstance()
                    openFragment(currentFragment)
                    return@OnNavigationItemSelectedListener true
                }

                currentFragment = M4x1profile.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    //Checks if user is online -> reused code
    //Source: https://stackoverflow.com/questions/53532406/activenetworkinfo-type-is-deprecated-in-api-level-28
    private fun isInternetAvailable(context: Context): Boolean {
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