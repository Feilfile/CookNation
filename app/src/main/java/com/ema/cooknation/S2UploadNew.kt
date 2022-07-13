package com.ema.cooknation

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.ema.cooknation.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.sql.Timestamp

class S2UploadNew : AppCompatActivity() {
    private lateinit var inputTitle:TextInputEditText
    private lateinit var inputDirections:TextInputEditText
    private lateinit var inputIngredients:TextInputEditText
    private lateinit var btnUpload: Button
    private lateinit var btnSelect: ImageButton
    private lateinit var filepath : Uri
    private lateinit var bitmap : Bitmap
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s2_upload_new)
        initializeVariables()
        setContent()
    }

    private fun initializeVariables() {
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        inputTitle = findViewById(R.id.etRecipeName)
        inputDirections = findViewById(R.id.etDirections)
        inputIngredients = findViewById(R.id.etIngredients)
        btnUpload = findViewById(R.id.btnUpload)
        btnSelect = findViewById(R.id.ibPreview)
    }

    private fun setContent() {
        btnSelect.setOnClickListener{
            selectImage()
        }

        btnUpload.setOnClickListener{
            uploadRecipe()
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
            val iv = findViewById<ImageView>(R.id.ibPreview)
            iv?.setImageBitmap(resized)
        }
    }


    private fun uploadRecipe() {
        //Checks if all fields are filled with content

        if(TextUtils.isEmpty(inputTitle.text) || TextUtils.isEmpty(inputDirections.text) || TextUtils.isEmpty(inputIngredients.text) || !this::filepath.isInitialized) {
            Toast.makeText(
                this,
                "Empty fields are not allowed!",
                Toast.LENGTH_SHORT
            ).show()
        } else  {
            writeInFirestore()
        }
    }

    private fun uploadPicture(docId: String) {

        //saves the selected picture inside the Firebase Storage
        val storageRef = FirebaseStorage.getInstance()
            .getReference("Recipes/${mAuth.uid.toString()}/${docId}")
        storageRef.putFile(filepath)
            .addOnCompleteListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                addUsernameAndIdToCollection(docId)
            }.addOnFailureListener {
                Log.e("Storage: ", "Error while saving Picture")
                Toast.makeText(
                    this,
                    "Error: Recipe couldn't be added",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun writeInFirestore() {
        val uid = mAuth.uid.toString()
        val date = getCurrentDate()
        val recipe = hashMapOf(
            //TODO: add current date -> Date Format and ingredients -> Array Format
            "uid" to uid,
            "author" to null,
            "title" to inputTitle.text.toString(),
            "date" to date,
            "picturePath" to null,
            "directions" to inputDirections.text.toString(),
            "ingredients" to inputIngredients.text.toString(),
            "ratingCount" to 0,
            "avgRating" to 0.0,
            "docId" to null
        )
        //creates the record inside the recipe collection in Firestore, username and ID will be added afterwards
        db.collection("recipes").add(recipe)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                uploadPicture(it.id)
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error writing document", e)
                Toast.makeText(
                    this,
                    "Error: Recipe couldn't be added",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun addUsernameAndIdToCollection(docId: String) {
        db.collection("user")
            .document(mAuth.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                Log.d("UserDataBase", "User Successfully found")
                val user = document.toObject<User>()
                val author = user?.username.toString()
                db.collection("recipes")
                    .document(docId)
                    .update("author", author,
                    "docId", docId,
                    "picturePath", "/Recipes/${mAuth.uid.toString()}/${docId}")
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Recipe successfully added!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
            } .addOnFailureListener{
                Log.e(ContentValues.TAG, "Username and DocId couldn't be added")
                Toast.makeText(
                    this,
                    "Error: Recipe couldn't be added",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun getCurrentDate(): Timestamp {
        val tsLong : Long = System.currentTimeMillis()
        return Timestamp(tsLong)
    }

}