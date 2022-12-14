package com.ema.cooknation.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ema.cooknation.R
import com.ema.cooknation.viewmodel.LocalRecipeViewModel
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.text.DateFormat

/**
 * Offline version of RecipeViewActivity, that opens if you don't have a internet connection
 */

class RecipeViewActivityOffline: AppCompatActivity() {

    private lateinit var recipeId: String
    private lateinit var avgRating: MaterialRatingBar
    private lateinit var numRating: TextView
    private lateinit var recipeTitle: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var recipeAuthor: TextView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeDirections: TextView
    private lateinit var recipeDate: TextView
    private lateinit var recipeDifficulty: TextView
    private lateinit var recipePrepTime: TextView

    private lateinit var localRecipeViewModel: LocalRecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_view_offline)
        initializeVariables()
        setContent()
    }

    private fun initializeVariables() {
        recipeId = intent.extras?.get("recipeId") as String
        localRecipeViewModel = ViewModelProvider(this)[LocalRecipeViewModel::class.java]
        recipeTitle = findViewById(R.id.tvRecipeNameOffline)
        recipeImage = findViewById(R.id.ivRecipeImgOffline)
        recipeAuthor = findViewById(R.id.tvAuthorOffline)
        recipeIngredients = findViewById(R.id.tvIngredientsOffline)
        recipeDirections = findViewById(R.id.tvDirectionsOffline)
        recipeDate = findViewById(R.id.tvDateOffline)
        recipeDifficulty = findViewById(R.id.tvDifficultyOffline)
        recipePrepTime = findViewById(R.id.tvPrepTimeOffline)
        avgRating = findViewById(R.id.mrbAvgRatingOffline)
        numRating = findViewById(R.id.tvNumRatingsOffline)
    }

    private fun setContent() {
        localRecipeViewModel.selecteLocalRecipe(recipeId).observe(this) {
            //converting bytearray into bitmap
            val bitmap = BitmapFactory.decodeByteArray(it.picture, 0, it.picture.size)
            recipeImage.setImageBitmap(bitmap)
            recipeTitle.text = it.title
            recipeAuthor.text = it.author
            recipeIngredients.text = it.ingredients
            recipeDirections.text = it.directions
            recipeDate.text = DateFormat.getDateInstance().format(it.date)
            recipeDifficulty.text = it.difficulty
            recipePrepTime.text = it.prepTime
            numRating.text = it.ratingCount.toString()
            avgRating.rating = it.avgRating
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}