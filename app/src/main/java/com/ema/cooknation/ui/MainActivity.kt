package com.ema.cooknation.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ema.cooknation.InternetValidator
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
        openFragment(HomeFragment.newInstance())
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView1, fragment)
        transaction.commit()
    }

    fun openUploadFragment() {
        startActivity(Intent(this@MainActivity, UploadActivity::class.java))
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    fun performLogout() {
        val registerLogin = RegisterLoginFragment.newInstance()
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
        val connectedToInternet = InternetValidator.isInternetAvailable(this)
        if (!connectedToInternet && item.itemId != R.id.m1_home) {
            currentFragment = OfflineScreen.newInstance()
            openFragment(currentFragment)
            return@OnNavigationItemSelectedListener true
        }
        when (item.itemId) {
            R.id.m1_home -> {
                currentFragment = HomeFragment.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m2_explore -> {
                currentFragment = ExploreFragment.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m3_search -> {
                currentFragment = SearchFragment.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m4_profile -> {
                // check if user is logged in
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    currentFragment = RegisterLoginFragment.newInstance()
                    openFragment(currentFragment)
                    return@OnNavigationItemSelectedListener true
                }

                currentFragment = ProfileFragment.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}