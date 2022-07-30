package com.ema.cooknation.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ema.cooknation.validator.InternetValidator
import com.ema.cooknation.R
import com.ema.cooknation.data.LocalRecipe
import com.ema.cooknation.data.Recipe
import com.ema.cooknation.viewmodel.LocalRecipeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File

class LoadingScreenActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var localRecipeViewModel: LocalRecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_screen)
        if (InternetValidator.isInternetAvailable(applicationContext) && FirebaseAuth.getInstance().uid != null) {
            initializeVariables()
            updateLocalDatabase()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            loadIntoMainActivity(intent)
        }
    }
    private fun initializeVariables(){
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        localRecipeViewModel = ViewModelProvider(this)[LocalRecipeViewModel::class.java]
    }

    private fun updateLocalDatabase() {
        var favoritesList: MutableList<String>
        var oldFavoritesList: MutableList<String>
        val intent = Intent(this, MainActivity::class.java)
        runBlocking {
            favoritesList = getNewFavoriteList()
            oldFavoritesList = getOldFavoriteList()
            val removeRecipes = oldFavoritesList.plus(favoritesList.toSet())
            val updatingRecipes = favoritesList.minus(oldFavoritesList.toSet())
            deleteJob(removeRecipes)
            updatingJob(updatingRecipes)
            loadIntoMainActivity(intent)
        }
    }

    private suspend fun getNewFavoriteList (): MutableList<String> {
        val favoritesList: MutableList<String> = arrayListOf()
        val resultFavourites = db.collection("favorites")
            .document(mAuth.uid.toString())
            .collection("docId")
            .get()
            .await()
        if (resultFavourites.isEmpty) {
            loadIntoMainActivity(intent)
        }
        for (document in resultFavourites) {
            favoritesList.add(document.id)
        }
        return favoritesList
    }

    private suspend fun getOldFavoriteList(): MutableList<String> {
        val oldFavoritesList: MutableList<String> = mutableListOf()
        GlobalScope.launch {
            val templist = localRecipeViewModel.getAllRecipesAtOnce()
            for (localRecips in templist) {
                oldFavoritesList.add(localRecips.docId)
            }
        }.join()
        return oldFavoritesList
    }

    private fun deleteJob(removeRecipes: List<String>){
        for (rRecipe in removeRecipes) {
            localRecipeViewModel.deleteLocalRecipe(rRecipe)
        }
    }

    private suspend fun updatingJob(updatingRecipes: List<String>) {
        val updatingJob = GlobalScope.launch(Dispatchers.IO) {
            for (uRecipe in updatingRecipes) {
                val recipeCheck = db.collection("recipes")
                    .document(uRecipe)
                    .get()
                    .await()
                if (!recipeCheck.exists()) {
                    db.collection("favorites")
                        .document(mAuth.uid.toString())
                        .collection("docId")
                        .document(recipeCheck.id)
                        .delete()
                    continue
                }
                val recipe = recipeCheck.toObject(Recipe::class.java)!!
                val storageRef = FirebaseStorage
                    .getInstance()
                    .getReference(recipe.picturePath.toString().drop(1))
                val localFile = File.createTempFile("tempFile", ".jpg")
                storageRef.getFile(localFile)
                    .addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

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
            }
        }
        updatingJob.join()
    }

    private fun loadIntoMainActivity(intent: Intent) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            finish()
        }, 2000) // Load into main activity after 2 seconds
    }
}
