package com.ema.cooknation.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ema.cooknation.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var etRegEmail: TextInputEditText
    private lateinit var etRegUsername : TextInputEditText
    private lateinit var etRegPassword: TextInputEditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        etRegEmail = findViewById(R.id.etRegEmail)
        etRegUsername = findViewById(R.id.etRegUsername)
        etRegPassword = findViewById(R.id.etRegPass)
        val tvLoginHere = findViewById<TextView>(R.id.tvLoginHere)
        val btnRegister = findViewById<TextView>(R.id.btnRegister)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        btnRegister.setOnClickListener {
            val email = etRegEmail.text.toString()
            val username = etRegUsername.text.toString()
            val password = etRegPassword.text.toString()
            createUser(email, username, password) }
        tvLoginHere.setOnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                )
            )
        }
    }

    private fun createUser(email : String, username : String, password : String) {
        when {
            //Checks if all required fields are filled
            TextUtils.isEmpty(email) -> {
                etRegEmail.error = "Email cannot be empty"
                etRegEmail.requestFocus()
            }
            TextUtils.isEmpty(username) -> {
                etRegUsername.error = "Username cannot be empty"
                etRegUsername.requestFocus()
            }
            TextUtils.isEmpty(password) -> {
                etRegPassword.error = "Password cannot be empty"
                etRegPassword.requestFocus()
            }
            else -> {
                //Checks if the username is already taken (in any lower/uppercase format)
                db.collection("user")
                    .whereEqualTo("username".lowercase(), username.lowercase())
                    .get()
                    .addOnSuccessListener{documents ->
                        if (documents.isEmpty) {
                            Log.v("Logger", "Success")
                            //Tries to create the user if email and password is valid + creates user in user collection with additional data
                            mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "User registered successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        addUserInCollection(email, username)
                                        startActivity(
                                            Intent(
                                                this@RegisterActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                     } else {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Registration Error" + task.exception!!.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                     }
                                }
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Username already taken",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }
    }

    private fun addUserInCollection (email: String, username:String) {
        val newUser = hashMapOf(
            "email" to email,
            "username" to username,
        )
        db.collection("user").document(mAuth.currentUser?.uid.toString())
            .set(newUser)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}