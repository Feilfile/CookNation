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
        openFragment(M1Home.newInstance("a","b"))
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView1, fragment)
        transaction.commit()
    }

    fun openUploadFragment() {
        val uploadFragment = S2Upload.newInstance("a", "a")
        openFragment(uploadFragment)
    }

    fun performLogout() {
        val registerLogin = M4x2RegisterLogin.newInstance("", "")
        openFragment(registerLogin)
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
                openFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m2_explore -> {
                val emptyFragment = EmptyFragment.newInstance("","")
                val exploreFragment = M2Explore.newInstance("c","d")
                openFragment(emptyFragment)
                openFragment(exploreFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m3_search -> {
                val emptyFragment = EmptyFragment.newInstance("","")
                val searchFragment = M3Search.newInstance("e","f")
                openFragment(emptyFragment)
                openFragment(searchFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m4_profile -> {
                // check if uer i logged in
                val user = mAuth!!.currentUser
                if (user == null) {
                    val loginRegisterFragment = M4x2RegisterLogin.newInstance("a","b")
                    openFragment(loginRegisterFragment)
                    return@OnNavigationItemSelectedListener true
                }

                val profileFragment = M4x1profile.newInstance("g","h")
                openFragment(profileFragment)
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