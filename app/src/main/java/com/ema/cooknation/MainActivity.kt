package com.ema.cooknation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        mAuth = FirebaseAuth.getInstance()
        val btnSignOut = findViewById<Button>(R.id.btnSignOut)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentContainerView1)
        /*val appBarConfiguration = AppBarConfiguration(setOf(R.id.m1_home, R.id.m2_explore, R.id.m3_search, R.id.m4_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)*/
        bottomNavigationView.setupWithNavController(navController)

    }



    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }
}