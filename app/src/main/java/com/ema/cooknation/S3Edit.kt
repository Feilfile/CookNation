package com.ema.cooknation

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var progressBar: ProgressBar

    private lateinit var db : FirebaseFirestore
    private lateinit var filepath : Uri
    private lateinit var bitmap : Bitmap

    private lateinit var difficultyDropdownMenu: AutoCompleteTextView
    private lateinit var prepTimeDropdownMenu: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s3_edit)
        initializeVariable()
        setValues()
        setUpDropdownTextBoxes()
    }

    private fun initializeVariable() {
        db = Firebase.firestore
        oldRecipe = intent.extras?.get("recipe") as Recipe
        editPicture = findViewById(R.id.ibEditPreview)
        editRecipeTitle = findViewById(R.id.itEditRecipeName)
        editDirections = findViewById(R.id.tiEditDirections)
        editIngredients = findViewById(R.id.tiEditIngredients)
        saveButton = findViewById(R.id.btnSaveEdit)
        progressBar = findViewById(R.id.pbEdit)
        oldRecipeTitle = oldRecipe.title.toString()
        uid = oldRecipe.uid.toString()

        difficultyDropdownMenu = findViewById(R.id.difficultyDropdownMenuEdit)
        prepTimeDropdownMenu = findViewById(R.id.prepTimeDropdownMenuEdit)
    }

    // Populates Dropdown Text Box with options to chose from
    private fun setUpDropdownTextBoxes() {
        val difficulties = resources.getStringArray(R.array.difficulties)
        val arrayAdapterDifficulties = ArrayAdapter(this, R.layout.dropdown_item, difficulties)
        difficultyDropdownMenu.setAdapter(arrayAdapterDifficulties)

        val prepTimes = resources.getStringArray(R.array.prepTimes)
        val arrayAdapterPrepTime = ArrayAdapter(this, R.layout.dropdown_item, prepTimes)
        prepTimeDropdownMenu.setAdapter(arrayAdapterPrepTime)
    }

    private fun setValues() {
        editRecipeTitle.setText(oldRecipe.title)
        editIngredients.setText(oldRecipe.ingredients)
        editDirections.setText(oldRecipe.directions)
        loadPictureInContainer(oldRecipe, editPicture)

        // leon
        difficultyDropdownMenu.setText(oldRecipe.difficulty)
        prepTimeDropdownMenu.setText(oldRecipe.prepTime)

        editPicture.setOnClickListener{
            selectImage()
        }
        saveButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            uploadPicture()
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

    private fun uploadPicture() {
        val storageRef = FirebaseStorage.getInstance().getReference("Recipes/${uid}/${oldRecipe.docId}")
        //checks if all text fields are filled
        val validation = RecipeInputValidator.validateInputs(applicationContext, editRecipeTitle.text.toString(), editDirections.text.toString(), editIngredients.text.toString(), true)
        if (!validation) {

        //filepath is only initialized when the picture is changed so if the picture isn't changed the resource hungry saving operation can be skipped
        } else if (this::filepath.isInitialized) {
            storageRef.putFile(filepath)
                .addOnCompleteListener {
                    Log.d("Storage: ", "Picture successfully saved")
                    editDocumentInfFirestore()
                }.addOnFailureListener {
                    Log.e("Storage: ", "Error while saving Picture")
                }
        } else {
            editDocumentInfFirestore()
        }
    }

    private fun editDocumentInfFirestore() {
        db.collection("recipes").document(oldRecipe.docId.toString())
            .update(
                "title", editRecipeTitle.text.toString(),
                "directions", editDirections.text.toString(),
                "ingredients", editIngredients.text.toString(),
                "prepTime", prepTimeDropdownMenu.text.toString(),
                "difficulty", difficultyDropdownMenu.text.toString()
            )
            .addOnCompleteListener{
                Toast.makeText(
                    this,
                    "Recipe successfully updated!",
                    Toast.LENGTH_SHORT
                ).show()
                completeActivity()
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error while uploading in Firestore!",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun completeActivity() {
        val intent = Intent()
        intent.putExtra("filename", "${uid}.${editRecipeTitle}")
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun getCurrentDate(): Timestamp {
        val tsLong : Long = System.currentTimeMillis()
        return Timestamp(tsLong)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

}