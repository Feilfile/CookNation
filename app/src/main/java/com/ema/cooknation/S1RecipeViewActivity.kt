package com.ema.cooknation

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ema.cooknation.data.LocalRecipe
import com.ema.cooknation.model.Recipe
import com.ema.cooknation.viewmodel.LocalRecipeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.io.ByteArrayOutputStream
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
    private lateinit var recipeDifficulty: TextView
    private lateinit var recipePrepTime: TextView
    private lateinit var ratingButton: View
    private lateinit var editButton: ImageButton
    private lateinit var deleteButten: ImageButton
    private lateinit var favButton: FloatingActionButton
    private lateinit var bitmap: Bitmap
    private var isFavorite: Boolean = false

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var localRecipeViewModel: LocalRecipeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s1_recipe_view)
        initializeVariables()
        loadData()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("S1RecipeViewActivity", "Reload")
        isFavorite = false
        loadData()
    }

    private fun initializeVariables() {
        recipeId = intent.extras?.get("recipeId") as String
        localRecipeViewModel =  ViewModelProvider(this).get(LocalRecipeViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        recipeTitle = findViewById(R.id.tvRecipeName)
        recipeImage = findViewById(R.id.ivRecipeImg)
        recipeAuthor = findViewById(R.id.tvAuthor)
        recipeIngredients = findViewById(R.id.tvIngredients)
        recipeDirections = findViewById(R.id.tvDirections)
        recipeDate = findViewById(R.id.tvDate)
        recipeDifficulty = findViewById(R.id.tvDifficulty)
        recipePrepTime = findViewById(R.id.tvPrepTime)
        avgRating = findViewById(R.id.mrbAvgRating)
        numRating = findViewById(R.id.tvNumRatings)
        ratingButton = findViewById(R.id.vRatingButton)
        editButton = findViewById(R.id.ibEdit)
        deleteButten = findViewById(R.id.ibDelete)
        favButton = findViewById(R.id.fabFavButton)
    }

    // gets recipe collection and calls setContent()
    fun loadData() {
        db.collection("recipes")
            .document(recipeId)
            .get()
            .addOnSuccessListener { document ->
                recipe = document.toObject((Recipe::class.java))!!
                setContent()
            } .addOnFailureListener{
                finish()
            }
    }

    private fun setContent() {
        loadPictureInContainer(recipe, recipeImage)
        recipeTitle.text = recipe.title
        recipeAuthor.text = recipe.author
        recipeIngredients.text = recipe.ingredients
        recipeDirections.text = recipe.directions
        recipeDate.text = recipe.date.toString()
        recipeDifficulty.text = recipe.difficulty
        recipePrepTime.text = recipe.prepTime
        numRating.text = recipe.ratingCount.toString()
        avgRating.rating = recipe.avgRating

        // enable editing and deleting when the user opens his own recipes, disable rating on own recipe
        if (recipe.uid == mAuth.uid) {
            editButton.visibility = View.VISIBLE
            editButton.isClickable = true
            deleteButten.visibility = View.VISIBLE
            deleteButten.isClickable = true
            ratingButton.visibility = View.INVISIBLE

            // opens edit acticity
            editButton.setOnClickListener {
                intent = Intent(this@S1RecipeViewActivity, S3Edit::class.java)
                intent.putExtra("recipe", recipe)
                startActivity(intent)
            }

            // opens delete confirmation
            val bottomSheetPopupDelete = BottomSheetPopupDelete()
            deleteButten.setOnClickListener {
                bottomSheetPopupDelete.show(supportFragmentManager, "BottomSheetDialog")
            }
        }

        // opens rating dialogue
        val bottomSheetPopupRating = BottomSheetPopupRating()
        ratingButton.setOnClickListener{
            bottomSheetPopupRating.show(supportFragmentManager, "BottomSheetDialog")
        }

        // adds or deletes recipe locally and toggles favorite button
        favButton.setOnClickListener {
            addToLocalDataBase()
            toggleFavoriteButton()
        }
    }

    // checks if currently viewed document is in favorites collection
    // of user and if so, sets flag to true and sets image resource
    private fun checkFavorite() {
        val docRef = db.collection("favorites")
            .document(mAuth.uid.toString())
            .collection("docId")
            .document(recipe.docId.toString())
        docRef.get().addOnSuccessListener {
            if(it.exists()) {
                isFavorite = true
                Log.d("FAVORITE", "NOW TRUE")
                favButton.setImageResource(R.drawable.ic_baseline_bookmark_filled_24)
            }
        }
    }

    // checks flag, toggles the drawable and either deletes or adds them to favorites ( + locally )
    private fun toggleFavoriteButton() {
        if (isFavorite) {
            isFavorite = false
            Log.d("FAVORITE", "NOW FALSE")
            Toast.makeText(this, "Recipe is now deleted from bookmarks", Toast.LENGTH_SHORT).show()
            deleteFavorite()
            favButton.setImageResource(R.drawable.ic_baseline_bookmark_empty_24)
            localRecipeViewModel.deleteLocalRecipe(recipe.docId.toString())
            //disable button for 1 sec to minimize errors in database
            favButton.setClickable(false)
            Handler(Looper.getMainLooper()).postDelayed({
                favButton.setClickable(true)

            }, 1000)
        } else {
            isFavorite = true
            Log.d("FAVORITE", "NOW TRUE")
            favButton.setImageResource(R.drawable.ic_baseline_bookmark_filled_24)
            Toast.makeText(this, "Recipe is now bookmarked", Toast.LENGTH_SHORT).show()
            addToLocalDataBase()
            addToFavorite()

            //disable button for 1 sec to minimize errors in database
            favButton.setClickable(false)
            Handler(Looper.getMainLooper()).postDelayed({
                favButton.setClickable(true)

            }, 1000)
        }
    }

    private fun deleteFavorite() {
        db.collection("favorites")
            .document(mAuth.uid.toString())
            .collection("docId")
            .document(recipe.docId.toString())
            .delete()
    }


    // TODO: Matthias comment pls
    private fun addToLocalDataBase() {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)

        val localRecipe = LocalRecipe(
            recipe.docId.toString(),
            recipe.uid.toString(),
            recipe.title.toString(),
            recipe.author.toString(),
            recipe.date!!.time,
            bos.toByteArray(),
            recipe.directions.toString(),
            recipe.ingredients.toString(),
            recipe.ratingCount,
            recipe.avgRating,
            recipe.prepTime.toString(),
            recipe.difficulty.toString()
        )
        localRecipeViewModel.addLocalRecipe(localRecipe)
    }

    private fun addToFavorite() {
        val data = hashMapOf("docId" to recipe.docId.toString())
        db.collection("favorites")
            .document(mAuth.uid.toString())
            .collection("docId")
            .document(recipe.docId.toString())
            .set(data)
    }

    // TODO: Matthias comment pls
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null) {
                recipeId = data.extras?.get("recipeId") as String
            }
            Log.d("resultLauncher", recipeId)
            loadData()
        }
    }

    // TODO: Matthias comment pls
    private fun loadPictureInContainer (recipe: Recipe, view: ImageView) {
        //drop 1 to prevent a double "/"
        val storageRef = FirebaseStorage.getInstance().getReference(recipe.picturePath.toString().drop(1))
        val localFile = File.createTempFile("tempFile", ".jpg")
        storageRef.getFile(localFile)
            .addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                view.setImageBitmap(bitmap)
                checkFavorite()
            }.addOnFailureListener{
                Log.e("pictureQuery", "error while loading picture from Firestore storage")
            }
    }

    fun getRecipe(): Recipe {
        return recipe
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}