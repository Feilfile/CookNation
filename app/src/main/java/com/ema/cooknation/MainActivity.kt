package com.ema.cooknation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CookNation)
        setContentView(R.layout.main_activity)
        mAuth = FirebaseAuth.getInstance()


        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener)
        //load initial fragment -> here m1_home
        openFragment(M1Home.newInstance("a","b"), false)
    }

    private fun openFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView1, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun openUploadFragment(addToBackStack: Boolean) {
        val uploadFragment = S2Upload.newInstance("a", "a")
        openFragment(uploadFragment, addToBackStack)
    }

    fun performLogout(addToBackStack: Boolean) {
        val registerLogin = M4x2RegisterLogin.newInstance("", "")
        openFragment(registerLogin, addToBackStack)
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
                val homeFragment = M1Home.newInstance("a","b")
                openFragment(homeFragment, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m2_explore -> {
                val exploreFragment = M2Explore.newInstance("c","d")
                openFragment(exploreFragment, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m3_search -> {
                val searchFragment = M3Search.newInstance("e","f")
                openFragment(searchFragment, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m4_profile -> {
                // check if uer i logged in
                val user = mAuth!!.currentUser
                if (user == null) {
                    val loginRegisterFragment = M4x2RegisterLogin.newInstance("a","b")
                    openFragment(loginRegisterFragment, false)
                    return@OnNavigationItemSelectedListener true
                }

                val profileFragment = M4x1profile.newInstance("g","h")
                openFragment(profileFragment, false)
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