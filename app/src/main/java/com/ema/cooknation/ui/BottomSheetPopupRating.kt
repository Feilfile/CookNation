package com.ema.cooknation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.ema.cooknation.R
import com.ema.cooknation.data.Rating
import com.ema.cooknation.data.Recipe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
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
        val rootView = inflater.inflate(R.layout.bottomsheet_popup_rating, container, false)
        ratingStars = rootView.findViewById(R.id.mrbUserRating)
        commitButton = rootView.findViewById(R.id.btnCommitRating)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        recipe = (activity as RecipeViewActivity).getRecipe()
        documentId = "${recipe.uid}.${recipe.title}"
        checkForExistingRating()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRatingState()
        commitButton.setOnClickListener {
            toggleCommitButton(false)
            if (alreadyRated) {
                editRating()
            } else {
                addNewRating()
            }
        }

    }

    /**
     * if rating is < 1 return false
     * */
    private fun minRating(): Boolean {
        return if(ratingStars.rating == 0f) {
            Toast.makeText(
                (activity as RecipeViewActivity),
                "Rating needs to be at least 1 Star",
                Toast.LENGTH_SHORT
            ).show()
            toggleCommitButton(true)
            true
        } else {
            false
        }
    }

    /**
     * if the user has not rated the recipe yet, show 0 stars, otherwise show the existing rating
     * */
    private fun initializeRatingState() {
        if (!alreadyRated) {
            ratingStars.rating = 0f
        } else {
            ratingStars.rating = oldRating.toFloat()
        }
    }

    private fun addNewRating() {
        if (minRating()) return
        val userRating = ratingStars.rating.toInt()
        recipe.addNewRating(userRating)
        updateRating(userRating)
    }

    private fun editRating() {
        if (minRating()) return
        val userRating = ratingStars.rating.toInt()
        recipe.updateExistingRating(oldRating, userRating)
        updateRating(userRating)
    }

    private fun updateRating(userRating: Int) {
        runBlocking {
            if (checkIfRecipeStillExists()) {
                editInRatingCollection(userRating)
                updateRecipeInCollection()
            } else {
                (activity as RecipeViewActivity).finish()
            }
        }
    }

    private suspend fun checkIfRecipeStillExists(): Boolean{
        val document = db.collection("recipes")
            .document(recipe.docId.toString())
            .get()
            .await()
            Log.e("TESTsfe", document.exists().toString())
        return document.exists()
    }

    /**
     * check if existing rating is in database and change alreadyRated variable accordingly
     * */
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

    /**
     * update rating and dismiss fragment
     * */
    private suspend fun updateRecipeInCollection() {
        db.collection("recipes")
            .document(recipe.docId.toString())
            .update(
                "avgRating", recipe.avgRating,
                "ratingCount", recipe.ratingCount
            )
            .await()
        (activity as RecipeViewActivity).loadData()
        dismiss()
    }

    /**
     * rating document gets added/updated inside the Firestore collection
     * */
    private suspend fun editInRatingCollection(userRating: Int) {
        val rating = hashMapOf(
            "ratingStars" to userRating
        )
        db.collection("rating")
            .document("${mAuth.uid}.${recipe.docId}")
            .set(rating)
            .await()
    }

    private fun toggleCommitButton(state: Boolean) {
        commitButton.isEnabled = state
        commitButton.isClickable = state
    }
}

