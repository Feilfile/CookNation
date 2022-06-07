package com.ema.cooknation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ema.cooknation.model.Recipe
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class S1RecipeViewActivity : AppCompatActivity() {
    lateinit var recipe: Recipe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s1_recipe_view)
        recipe = intent.extras?.get("recipe") as Recipe
        val recipeTitle = findViewById<TextView>(R.id.tvRecipeName)
        val recipeImage = findViewById<ImageView>(R.id.ivRecipeImg)
        val recipeAuthor = findViewById<TextView>(R.id.tvAuthor)
        val recipeIngredients = findViewById<TextView>(R.id.tvIngredients)
        val recipeDirections = findViewById<TextView>(R.id.tvDirections)
        val recipeDate = findViewById<TextView>(R.id.tvDate)


        loadPictureInContainer(recipe, recipeImage)
        recipeTitle.text = recipe.title.toString()
        recipeAuthor.text = recipe.author.toString()
        recipeIngredients.text = recipe.ingredients.toString()
        recipeDirections.text = recipe.directions.toString()
        recipeDate.text = recipe.date.toString()
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
                //TODO: implement OnFailureListener in loadPictureInContainer
            }
    }
}