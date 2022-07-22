package com.ema.cooknation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ema.cooknation.viewmodel.AppViewModel
import com.ema.cooknation.viewmodel.LocalRecipeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private lateinit var currentFragment: Fragment
    private lateinit var localRecipeViewModel: LocalRecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CookNation)
        setContentView(R.layout.main_activity)
        mAuth = FirebaseAuth.getInstance()
        localRecipeViewModel =  ViewModelProvider(this).get(LocalRecipeViewModel::class.java)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener)
        //load initial fragment -> here m1_home
        openFragment(M1Home.newInstance("",""))
        //val currentFragment = model.currentFragment.value
        //val username = model.username.value
        //val homeFragment = currentFragment.newInstance("e","f")
        /*model.currentFragment.observe(this) { fragment ->
            openFragment(fragment)
        }*/
    }
    //Resets Container when reloading RecipeView
    override fun onRestart() {
        super.onRestart()
        try {
            (currentFragment as M4x1profile).resetAdapter()
        } catch (e: Exception) {
        }

        try {
            (currentFragment as M3Search).resetAdapter()
        } catch (e: Exception) {
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView1, fragment)
        transaction.commit()
    }

    fun openUploadFragment() {
        startActivity(Intent(this@MainActivity, S2UploadNew::class.java))
    }

    fun performLogout() {
        val registerLogin = M4x2RegisterLogin.newInstance("", "")
        openFragment(registerLogin)
    }

    fun openWebViewActivity() {
        startActivity(Intent(this@MainActivity, WebViewActivity::class.java))
    }

    fun openLoginActivity() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }

    fun openRegisterActivity() {
        startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
    }

    //TODO: remove/change parsed fragment parameters

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.m1_home -> {
                currentFragment = M1Home.newInstance("","")
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m2_explore -> {
                currentFragment = M2Explore.newInstance("","")
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m3_search -> {
                currentFragment = M3Search.newInstance("","")
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m4_profile -> {
                // check if uer i logged in
                val user = mAuth!!.currentUser
                if (user == null) {
                    currentFragment = M4x2RegisterLogin.newInstance("","")
                    openFragment(currentFragment)
                    return@OnNavigationItemSelectedListener true
                }

                currentFragment = M4x1profile.newInstance("", "")
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }







    /*override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }*/
}