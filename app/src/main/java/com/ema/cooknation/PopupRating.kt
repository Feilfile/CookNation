package com.ema.cooknation

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ema.cooknation.model.Rating
import com.ema.cooknation.model.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class PopupRating : AppCompatActivity() {
    private lateinit var commitButton: Button
    private lateinit var ratingStars: MaterialRatingBar
    private var alreadyRated: Boolean = false
    private var oldRating: Int = 5
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
        checkForExistingRating()
        ratingStars = findViewById(R.id.mrbUserRating)
        commitButton = findViewById(R.id.btnCommitRating)
        /*if (!alreadyRated) {
            ratingStars.rating = 5f
        } else {
            ratingStars.rating = oldRating.toFloat()
        }*/

        commitButton.setOnClickListener {
            if(alreadyRated) {
                editRating()
            } else {
                addNewRating()
            }
        }

    }

    private fun addNewRating() {
        val userRating = ratingStars.rating.toInt()
        recipe.addNewRating(userRating)
        editInRatingCollection(userRating)
        updateRecipeInCollection()
    }

    private fun editRating() {
        val userRating = ratingStars.rating.toInt()
        recipe.updateExistingRating(oldRating, userRating)
        editInRatingCollection(userRating)
        updateRecipeInCollection()
    }

    private fun checkForExistingRating() {
        db.collection("rating")
            .document("${mAuth.uid}.${documentId}")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    alreadyRated = true
                    oldRating = document.toObject<Rating>()!!.ratingStars
                    ratingStars.rating = oldRating.toFloat()
                }
            }
    }

    private fun updateRecipeInCollection(){
        db.collection("recipes").document(documentId)
            .update(
                "avgRating", recipe.avgRating,
                "ratingCount", recipe.ratingCount
            )
        finish()
    }

    private fun editInRatingCollection(userRating: Int) {
        val rating = hashMapOf(
            "ratingStars" to userRating
        )
        db.collection("rating").document("${mAuth.uid}.${documentId}")
            .set(rating)
    }
}