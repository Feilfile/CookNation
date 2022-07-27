package com.ema.cooknation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var etLoginEmail: TextInputEditText? = null
    private var etLoginPassword: TextInputEditText? = null
    private var mAuth: FirebaseAuth? = null
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.s4_login)
        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPass)
        val tvRegisterHere = findViewById<TextView>(R.id.tvRegisterHere)
        val btnLogin = findViewById<TextView>(R.id.btnLogin)
        mAuth = FirebaseAuth.getInstance()
        btnLogin.setOnClickListener {
            val email = etLoginEmail!!.text.toString()
            val password = etLoginPassword!!.text.toString()
            loginUser(email, password)
        }
        tvRegisterHere.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        }
    }

    // check if login was successful and if so, change activity
    private fun loginUser(email: String, password: String) {
        when {
            TextUtils.isEmpty(email) -> {
                etLoginEmail!!.error = "Email cannot be empty"
                etLoginEmail!!.requestFocus()
            }
            TextUtils.isEmpty(password) -> {
                etLoginPassword!!.error = "Password cannot be empty"
                etLoginPassword!!.requestFocus()
            }
            else -> {
                mAuth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@LoginActivity,
                            "User logged in successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Error\n" + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}