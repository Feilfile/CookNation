package com.ema.cooknation

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.ema.cooknation.model.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class PopupRating : AppCompatActivity() {
    private lateinit var commitButton: Button
    private lateinit var ratingStars: MaterialRatingBar
    private var alreadyRated: Boolean = false
    private lateinit var documentId : String
    private lateinit var recipe: Recipe

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_rating)

        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        recipe = intent.extras?.get("recipe") as Recipe
        documentId = "${recipe.uid}.${recipe.title}"
        ratingStars = findViewById(R.id.mrbUserRating)
        commitButton = findViewById(R.id.btnCommitRating)
        checkForExistingRating()
        if (!alreadyRated) {
            ratingStars.rating = 0f
        } else {
            getUserRating()
        }

        commitButton.setOnClickListener {
            addNewRating()
        }

    }

    private fun checkForExistingRating() {
    }

    private fun addNewRating() {
        val userRating = ratingStars.rating
        addInRatingCollection(userRating)
        recipe.addNewRating(userRating.toInt())
        val ratingData = hashMapOf(
            "avgRating" to recipe.avgRating,
            "ratingCount" to recipe.numRatings
        )
        db.collection("recipes").document(documentId)
            .update(ratingData as Map<String, Any>)
        finish()
    }

    private fun editRating() {

    }

    private fun deleteRating() {

    }

    private fun addInRatingCollection(userRating: Float) {
        val rating = hashMapOf(
            "ratingStars" to userRating
        )
        db.collection("rating").document("${mAuth.uid}.${documentId}")
            .set(rating)
    }

    private fun addToRecipeRating() {

    }

    private fun getUserRating() {

    }
}