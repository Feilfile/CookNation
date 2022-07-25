package com.ema.cooknation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class LoadingScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_screen)

        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            finish()

        }, 3000)    // Shows Splash Screen for 3 seconds
    }
}