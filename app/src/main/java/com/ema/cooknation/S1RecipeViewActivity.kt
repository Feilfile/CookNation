package com.ema.cooknation

import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ema.cooknation.model.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.io.File

class S1RecipeViewActivity : AppCompatActivity() {
    private lateinit var recipeId: String
    private lateinit var recipe: Recipe
    private lateinit var avgRating: MaterialRatingBar
    private lateinit var numRating: TextView
    private lateinit var recipeTitle: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var recipeAuthor: TextView
    private lateinit var recipeIngredients: TextView
    private lateinit var recipeDirections: TextView
    private lateinit var recipeDate: TextView
    private lateinit var ratingButton: View
    private lateinit var editButton: ImageButton
    private lateinit var deleteButten: ImageButton

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    val bottomSheetPopupRating = BottomSheetPopupRating()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s1_recipe_view)
        recipeId = intent.extras?.get("recipeId") as String
        initializeVariables()
        refreshData()

    }

    override fun onRestart() {
        super.onRestart()
        refreshData()
    }

    private fun refreshData() {
        db.collection("recipes")
            .document(recipeId)
            .get()
            .addOnSuccessListener { document ->
                recipe = document.toObject((Recipe::class.java))!!
                setContent()
                ratingButton.setOnClickListener{
                    bottomSheetPopupRating.show(supportFragmentManager, "BottomSheetDialog")
                    /*intent = Intent(this@S1RecipeViewActivity, PopupRating::class.java)
                    intent.putExtra("recipe", recipe)
                    startActivity(intent)*/
                }
            } .addOnFailureListener{
                finish()
            }
    }

    private fun initializeVariables() {
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        recipeTitle = findViewById(R.id.tvRecipeName)
        recipeImage = findViewById(R.id.ivRecipeImg)
        recipeAuthor = findViewById(R.id.tvAuthor)
        recipeIngredients = findViewById(R.id.tvIngredients)
        recipeDirections = findViewById(R.id.tvDirections)
        recipeDate = findViewById(R.id.tvDate)
        avgRating = findViewById(R.id.mrbAvgRating)
        numRating = findViewById(R.id.tvNumRatings)
        ratingButton = findViewById(R.id.vRatingButton)
        editButton = findViewById(R.id.ibEdit)
        deleteButten = findViewById(R.id.ibDelete)
    }

    private fun setContent() {
        loadPictureInContainer(recipe, recipeImage)
        recipeTitle.text = recipe.title.toString()
        recipeAuthor.text = recipe.author.toString()
        recipeIngredients.text = recipe.ingredients.toString()
        recipeDirections.text = recipe.directions.toString()
        recipeDate.text = recipe.date.toString()
        numRating.text = recipe.ratingCount.toString()
        avgRating.rating = recipe.avgRating
        //enable editing and deleting when the User opens his own recipes
        if (recipe.uid == mAuth.uid) {
            editButton.visibility = View.VISIBLE
            editButton.isClickable = true
            deleteButten.visibility = View.VISIBLE
            deleteButten.isClickable = true
        }
        editButton.setOnClickListener{
            intent = Intent(this@S1RecipeViewActivity, S3Edit::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }
        deleteButten.setOnClickListener{

        }
    }


    private fun loadPictureInContainer (recipe: Recipe, view: ImageView) {
        //drop 1 to prevent a double "/"
        val storageRef = FirebaseStorage.getInstance().getReference(recipe.picturePath.toString().drop(1))
        Log.d("SSSSSSSSSSSSSSSS", storageRef.toString())
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