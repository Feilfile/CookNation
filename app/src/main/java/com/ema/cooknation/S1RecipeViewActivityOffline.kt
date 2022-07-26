package com.ema.cooknation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ema.cooknation.viewmodel.LocalRecipeViewModel
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class S1RecipeViewActivityOffline: AppCompatActivity() {

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
        setContentView(R.layout.activity_s1_recipe_view_offline)
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
            val bitmap = BitmapFactory.decodeByteArray(it.picture, 0, it.picture.size)
            recipeImage.setImageBitmap(bitmap)
            recipeTitle.text = it.title
            recipeAuthor.text = it.author
            recipeIngredients.text = it.ingredients
            recipeDirections.text = it.directions
            recipeDate.text = it.date.toString()
            recipeDifficulty.text = it.difficulty
            recipePrepTime.text = it.prepTime
            numRating.text = it.ratingCount.toString()
            avgRating.rating = it.avgRating
        }
    }
}