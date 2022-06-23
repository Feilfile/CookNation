package com.ema.cooknation

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ema.cooknation.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.sql.Timestamp


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [S2Upload.newInstance] factory method to
 * create an instance of this fragment.
 */
class S2Upload : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var filepath : Uri
    private lateinit var bitmap : Bitmap
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.s2_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        val btnUpload = getView()?.findViewById<Button>(R.id.btnUpload)
        val btnSelect = getView()?.findViewById<ImageButton>(R.id.ibPreview)

        btnSelect?.setOnClickListener{
            selectImage()
        }

        btnUpload?.setOnClickListener{
            uploadRecipe()
        }
    }

    private fun selectImage() {
        val i = Intent()
        i.type = "image/*"
        i.action =Intent.ACTION_GET_CONTENT
        startActivityForResult(i, 100)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 100 && resultCode === Activity.RESULT_OK && data != null) {
            filepath = data.data!!
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filepath)
            val resized = Bitmap.createScaledBitmap(bitmap, 960,640,false)
            val iv = view?.findViewById<ImageView>(R.id.ibPreview)
            iv?.setImageBitmap(resized)
        }
    }

    private fun addRecipe(inputTitle: String, inputDirections: String, inputIngredients: String) {

        val storageRef = FirebaseStorage.getInstance().getReference("Recipes/$inputTitle")
        storageRef.putFile(filepath).
                addOnCompleteListener{
                //TODO: add Listener

                }.addOnFailureListener{

        }
        val picturePath = FirebaseStorage.getInstance().getReference("Recipes/$inputTitle").path
        val date = getCurrentDate()
        val recipe = hashMapOf(
                //TODO: add current date -> Date Format and ingredients -> Array Format
            "uid" to mAuth.uid.toString(),
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
                Log.d(TAG, "DocumentSnapshot successfully written!")
                Toast.makeText(
                    activity,
                    "Recipe successfully added!",
                    Toast.LENGTH_SHORT
                ).show()
                addUserNameToCollection(inputTitle)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e)
                Toast.makeText(
                    activity,
                    "Error: Recipe couldn't be added",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadRecipe() {
        val title = view?.findViewById<TextInputEditText>(R.id.etRecipeName)?.text.toString()
        val directions = view?.findViewById<TextInputEditText>(R.id.etDirections)?.text.toString()
        val ingredients = view?.findViewById<TextInputEditText>(R.id.etIngredients)?.text.toString()
        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(directions) || TextUtils.isEmpty(ingredients) || TextUtils.isEmpty(filepath.toString())) {
            Toast.makeText(
                activity,
                "Empty field not allowed!",
                Toast.LENGTH_SHORT
            ).show()
        } else  {
            addRecipe(title, directions, ingredients)
            //activity?.supportFragmentManager?.popBackStack()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment s2_upload.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            S2Upload().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            } .addOnFailureListener{ e ->
                Log.w(TAG, "Document not found", e)
            }

    }

    private fun getCurrentDate(): Timestamp {
        val tsLong : Long = System.currentTimeMillis()
        return Timestamp(tsLong)
    }
}

