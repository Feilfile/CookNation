package com.ema.cooknation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ema.cooknation.data.LocalRecipe
import com.ema.cooknation.model.Recipe
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
        if (isInternetAvailable(applicationContext)) {
            initializeVariables()
            updateLocalDatabase()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            finish()

        }, 3000)    // Shows Splash Screen for 3 seconds*/
        }
    }
    private fun initializeVariables(){
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        localRecipeViewModel = ViewModelProvider(this)[LocalRecipeViewModel::class.java]
    }

    private fun updateLocalDatabase() {
        getFavorites()
    }

    private fun getFavorites () {
        val favoritesList: MutableList<String> = arrayListOf()
        val oldFavoritesList: MutableList<String> = arrayListOf()
        val intent = Intent(this, MainActivity::class.java)
        runBlocking {
        val resultFavourites = db.collection("favorites")
            .document(mAuth.uid.toString())
            .collection("docId")
            .get()
            .await()
            for (document in resultFavourites) {
                favoritesList.add(document.id)
            }
                //OPERATION 2
            localRecipeViewModel.readAllData.observe(this@LoadingScreenActivity) { localRecipes ->
                for (recipe in localRecipes) {
                    oldFavoritesList.add(recipe.docId)
                }
            }
                //OPERATION 3
            val removeRecipes = oldFavoritesList.plus(favoritesList.toSet())
            val updatingRecipes = favoritesList.minus(oldFavoritesList.toSet())
            val deleteJob = GlobalScope.launch(Dispatchers.IO) {
                for (rRecipe in removeRecipes) {
                    localRecipeViewModel.deleteLocalRecipe(rRecipe)
                }
            }
            deleteJob.join()
            val updatingJob = GlobalScope.launch(Dispatchers.IO) {
                for (uRecipe in updatingRecipes) {
                    val recipe = db.collection("recipes")
                        .document(uRecipe)
                        .get()
                        .await()
                        .toObject(Recipe::class.java)!!
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
            Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            finish()
            }, 2000)
        }
    }





    private fun isInternetAvailable(context: Context): Boolean {
        val result: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result
    }
}
