package com.ema.cooknation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mAuth = FirebaseAuth.getInstance()


        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        //load initial fragment -> here m1_home
        openFragment(m1_home.newInstance("a","b"), false)
    }

    private fun openFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView1, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    public fun openUploadFragment(fragment: Fragment, addToBackStack:Boolean) {
        openFragment(fragment, addToBackStack)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.m1_home -> {
                val homeFragment = m1_home.newInstance("a","b")
                openFragment(homeFragment, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m2_explore -> {
                val exploreFragment = m2_explore.newInstance("c","d")
                openFragment(exploreFragment, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m3_search -> {
                val searchFragment = m3_search.newInstance("e","f")
                openFragment(searchFragment, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.m4_profile -> {
                // check if uer i logged in
                val user = mAuth!!.currentUser
                if (user == null) {
                    val loginRegiterFragment = m4_2_register_login.newInstance("a","b")
                    openFragment(loginRegiterFragment, false)
                    return@OnNavigationItemSelectedListener true
                }

                val profileFragment = m4_profile.newInstance("g","h")
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