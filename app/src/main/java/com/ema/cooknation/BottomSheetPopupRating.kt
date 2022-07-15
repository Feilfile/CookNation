package com.ema.cooknation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.ema.cooknation.model.Rating
import com.ema.cooknation.model.Recipe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class BottomSheetPopupRating : BottomSheetDialogFragment() {

    private lateinit var commitButton: Button
    private lateinit var ratingStars: MaterialRatingBar
    private var alreadyRated: Boolean = false
    private var oldRating: Int = 5
    private lateinit var documentId: String
    private lateinit var recipe: Recipe

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_popup_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        recipe = (activity as S1RecipeViewActivity).getRecipe()
        documentId = "${recipe.uid}.${recipe.title}"
        checkForExistingRating()
        ratingStars = view.findViewById(R.id.mrbUserRating)
        commitButton = view.findViewById(R.id.btnCommitRating)
        if (!alreadyRated) {
            ratingStars.rating = 5f
        } else {
            ratingStars.rating = oldRating.toFloat()
        }

        commitButton.setOnClickListener {
            if (alreadyRated) {
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
            .document("${mAuth.uid}.${recipe.docId}")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    alreadyRated = true
                    oldRating = document.toObject<Rating>()!!.ratingStars
                    ratingStars.rating = oldRating.toFloat()
                }
            }
    }

    private fun updateRecipeInCollection() {
        db.collection("recipes").document(recipe.docId.toString())
            .update(
                "avgRating", recipe.avgRating,
                "ratingCount", recipe.ratingCount
            )
        (activity as S1RecipeViewActivity).loadData()
        dismiss()
    }

    private fun editInRatingCollection(userRating: Int) {
        val rating = hashMapOf(
            "ratingStars" to userRating
        )
        db.collection("rating").document("${mAuth.uid}.${recipe.docId}")
            .set(rating)
    }
}

