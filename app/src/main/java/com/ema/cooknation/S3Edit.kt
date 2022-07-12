package com.ema.cooknation

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ema.cooknation.model.Recipe
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.sql.Timestamp

class S3Edit : AppCompatActivity() {
    private lateinit var oldRecipe: Recipe
    private lateinit var editPicture : ImageButton
    private lateinit var editRecipeTitle : TextInputEditText
    private lateinit var editDirections : TextInputEditText
    private lateinit var editIngredients : TextInputEditText
    private lateinit var saveButton : Button
    private lateinit var oldRecipeTitle : String
    private lateinit var uid: String

    private lateinit var db : FirebaseFirestore
    private lateinit var filepath : Uri
    private lateinit var bitmap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s3_edit)
        initializeVariable()
        setValues()
    }

    private fun initializeVariable() {
        db = Firebase.firestore
        oldRecipe = intent.extras?.get("recipe") as Recipe
        editPicture = findViewById(R.id.ibEditPreview)
        editRecipeTitle = findViewById(R.id.itEditRecipeName)
        editDirections = findViewById(R.id.tiEditDirections)
        editIngredients = findViewById(R.id.tiEditIngredients)
        saveButton = findViewById(R.id.btnSaveEdit)
        oldRecipeTitle = oldRecipe.title.toString()
        uid = oldRecipe.uid.toString()
    }

    private fun setValues() {
        editRecipeTitle.setText(oldRecipe.title)
        editIngredients.setText(oldRecipe.ingredients)
        editDirections.setText(oldRecipe.directions)
        loadPictureInContainer(oldRecipe, editPicture)
        editPicture.setOnClickListener{
            selectImage()
        }
        saveButton.setOnClickListener {
            val author = oldRecipe.author.toString()
            val avgRating = oldRecipe.avgRating
            val date = getCurrentDate()
            val ratingCount = oldRecipe.ratingCount
            val picturePath = FirebaseStorage.getInstance().getReference("Recipes/${uid}/${editRecipeTitle.text}").path
            if (editRecipeTitle.text.toString() == oldRecipeTitle) {
                uploadPicture(author, avgRating, date, ratingCount, picturePath)
            }
            //val picturePath = FirebaseStorage.getInstance().getReference("Recipes/$editRecipeTitle").path
            //deleteOldDocument()
            //saveNewDocument(author, avgRating, date, ratingCount, picturePath)
        }
    }

    private fun loadPictureInContainer (recipe: Recipe, view: ImageView) {
        //drop 1 to prevent a double "/"
        val storageRef = FirebaseStorage.getInstance().getReference(recipe.picturePath.toString().drop(1))
        val localFile = File.createTempFile("tempFile", ".jpg")
        storageRef.getFile(localFile)
            .addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                view.setImageBitmap(bitmap)
                filepath = storageRef.path.toUri()
            }.addOnFailureListener{
                Log.e("pictureQuery", "error while loading picture from Firestore storage")
            }
    }
    private fun selectImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(i, 100)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 100 && resultCode === Activity.RESULT_OK && data != null) {
            filepath = data.data!!
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            val resized = Bitmap.createScaledBitmap(bitmap, 960,640,false)
            editPicture.setImageBitmap(resized)
        }
    }

    private fun deleteOldPicture() {
        FirebaseStorage.getInstance().getReference("Recipes/$oldRecipeTitle").delete()
    }

    private fun uploadPicture(author: String, avgRating: Float, date: Timestamp, ratingCount: Int, picturePath: String) {
        val storageRef = FirebaseStorage.getInstance().getReference("Recipes/${uid}/${editRecipeTitle.text}")
        storageRef.putFile(filepath)
            .addOnCompleteListener{
                Log.d("Storage: ", "Picture successfully saved")
                //nextOperation
                saveNewDocument(author, avgRating, date, ratingCount, picturePath)
        }.addOnFailureListener{
            Log.e("Storage: ", "Error while saving Picture")
        }
    }

    private fun deleteOldDocument() {
        db.collection("Recipes").document("${oldRecipe.uid}.${oldRecipeTitle}").delete()
    }

    private fun saveNewDocument(author: String, avgRating: Float, date: Timestamp, ratingCount: Int, picturePath: String) {
        val recipe = hashMapOf(
            //TODO: add current date -> Date Format and ingredients -> Array Format
            "uid" to uid,
            "author" to author,
            "title" to editRecipeTitle.text.toString(),
            "date" to date,
            "picturePath" to picturePath,
            "directions" to editDirections.text.toString(),
            "ingredients" to editIngredients.text.toString(),
            "ratingCount" to ratingCount,
            "avgRating" to avgRating
        )
        db.collection("recipes").document("${uid}.${editRecipeTitle.text.toString()}")
            .set(recipe)
            .addOnCompleteListener{
                finish()
            }
        Log.d("LLLLLLLLLLLLLLLL", "${uid}.${editRecipeTitle.text}")
        //db.collection("recipes").document("${uid}.${editRecipeTitle.text}").update("directions", editDirections.text)
            /*.addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                Toast.makeText(
                    this,
                    "Recipe successfully updated!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e)
                Toast.makeText(
                    this,
                    "Error: Recipe couldn't be updated",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
    }

    private fun getCurrentDate(): Timestamp {
        val tsLong : Long = System.currentTimeMillis()
        return Timestamp(tsLong)
    }


}