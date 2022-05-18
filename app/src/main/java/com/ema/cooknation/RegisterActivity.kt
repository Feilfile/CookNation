package com.ema.cooknation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private var etRegEmail: TextInputEditText? = null
    private var etRegPassword: TextInputEditText? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.s5_register)
        etRegEmail = findViewById(R.id.etRegEmail)
        etRegPassword = findViewById(R.id.etRegPass)
        val tvLoginHere = findViewById<TextView>(R.id.tvLoginHere)
        val btnRegister = findViewById<TextView>(R.id.btnRegister)
        mAuth = FirebaseAuth.getInstance()
        btnRegister.setOnClickListener { createUser() }
        tvLoginHere.setOnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                )
            )
        }
    }

    private fun createUser() {
        val email = etRegEmail!!.text.toString()
        val password = etRegPassword!!.text.toString()
        when {
            TextUtils.isEmpty(email) -> {
                etRegEmail!!.error = "Email cannot be empty"
                etRegEmail!!.requestFocus()
            }
            TextUtils.isEmpty(password) -> {
                etRegPassword!!.error = "Password cannot be empty"
                etRegPassword!!.requestFocus()
            }
            else -> {
                mAuth!!.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "User registered successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration Error" + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}