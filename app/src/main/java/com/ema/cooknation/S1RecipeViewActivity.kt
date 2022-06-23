package com.ema.cooknation

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ema.cooknation.model.Recipe
import com.google.firebase.storage.FirebaseStorage
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.io.File

class S1RecipeViewActivity : AppCompatActivity() {
    private lateinit var recipe: Recipe
    private lateinit var avgRating: MaterialRatingBar
    private lateinit var recipeTitle: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var recipeAuthor: TextView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeDirections: TextView
    private lateinit var recipeDate: TextView
    private lateinit var ratingButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s1_recipe_view)
        recipe = intent.extras?.get("recipe") as Recipe
        initializeVariables()
        setContent()
        ratingButton.setOnClickListener{
            intent = Intent(this@S1RecipeViewActivity, PopupRating::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }

    }

    private fun initializeVariables() {
        recipeTitle = findViewById(R.id.tvRecipeName)
        recipeImage = findViewById(R.id.ivRecipeImg)
        recipeAuthor = findViewById(R.id.tvAuthor)
        recipeIngredients = findViewById(R.id.tvIngredients)
        recipeDirections = findViewById(R.id.tvDirections)
        recipeDate = findViewById(R.id.tvDate)
        avgRating = findViewById(R.id.mrbAvgRating)
        ratingButton = findViewById(R.id.vRatingButton)
    }

    private fun setContent() {
        loadPictureInContainer(recipe, recipeImage)
        recipeTitle.text = recipe.title.toString()
        recipeAuthor.text = recipe.author.toString()
        recipeIngredients.text = recipe.ingredients.toString()
        recipeDirections.text = recipe.directions.toString()
        recipeDate.text = recipe.date.toString()
        //TODO: FIX FLOATING STARS
        avgRating.rating = recipe.avgRating.toFloat()
        //avgRating.setRating(recipe.avgRating.toFloat())
        Log.d("avgRating", recipe.avgRating.toString())
    }


    private fun loadPictureInContainer (recipe: Recipe, view: ImageView) {
        //drop 1 to prevent a double "/"
        val storageRef = FirebaseStorage.getInstance().getReference(recipe.picturePath.toString().drop(1))
        val localFile = File.createTempFile("tempFile", ".jpg")
        storageRef.getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                view.setImageBitmap(bitmap)
            }.addOnFailureListener{
                Log.e("pictureQuery", "error while loading picture from Firestore storage")
            }
    }
}