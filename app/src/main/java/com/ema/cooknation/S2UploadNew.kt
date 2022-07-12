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
    private lateinit var filepath : Uri
    private lateinit var bitmap : Bitmap
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s2_upload_new)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        val btnUpload = findViewById<Button>(R.id.btnUpload)
        val btnSelect = findViewById<ImageButton>(R.id.ibPreview)

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

    private fun addRecipe(inputTitle: String, inputDirections: String, inputIngredients: String) {

        val storageRef = FirebaseStorage.getInstance().getReference("Recipes/${mAuth.uid.toString()}/${inputTitle}")
        storageRef.putFile(filepath).
        addOnCompleteListener{
            Log.d("Storage: ", "Picture successfully saved")
        }.addOnFailureListener{
            Log.e("Storage: ", "Error while saving Picture")
        }
        val uid = mAuth.uid.toString()
        val picturePath = FirebaseStorage.getInstance().getReference("Recipes/${uid}/$inputTitle").path
        val date = getCurrentDate()
        val recipe = hashMapOf(
            //TODO: add current date -> Date Format and ingredients -> Array Format
            "uid" to uid,
            "author" to null,
            "title" to inputTitle,
            "date" to date,
            "picturePath" to picturePath,
            "directions" to inputDirections,
            "ingredients" to inputIngredients,
            "ratingCount" to 0,
            "avgRating" to 0.0
        )
        db.collection("recipes").document("${mAuth.uid}.$inputTitle")
            .set(recipe)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                Toast.makeText(
                    this,
                    "Recipe successfully added!",
                    Toast.LENGTH_SHORT
                ).show()
                addUserNameToCollection(inputTitle)
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e)
                Toast.makeText(
                    this,
                    "Error: Recipe couldn't be added",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadRecipe() {
        val title = findViewById<TextInputEditText>(R.id.etRecipeName)?.text.toString()
        val directions = findViewById<TextInputEditText>(R.id.etDirections)?.text.toString()
        val ingredients = findViewById<TextInputEditText>(R.id.etIngredients)?.text.toString()
        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(directions) || TextUtils.isEmpty(ingredients) || TextUtils.isEmpty(filepath.toString())) {
            Toast.makeText(
                this,
                "Empty field not allowed!",
                Toast.LENGTH_SHORT
            ).show()
        } else  {
            addRecipe(title, directions, ingredients)
            //activity?.supportFragmentManager?.popBackStack()
        }
    }

    private fun addUserNameToCollection(inputTitle: String) {
        db.collection("user")
            .document(mAuth.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                Log.d("UserDataBase", "User Successfully found")
                val user = document.toObject<User>()
                val author = user?.username.toString()
                db.collection("recipes")
                    .document("${mAuth.uid}.$inputTitle")
                    .update("author", author)
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                        finish()
                    }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
            } .addOnFailureListener{ e ->
                Log.w(ContentValues.TAG, "Document not found", e)
            }

    }

    private fun getCurrentDate(): Timestamp {
        val tsLong : Long = System.currentTimeMillis()
        return Timestamp(tsLong)
    }

}