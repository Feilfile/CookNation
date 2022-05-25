package com.ema.cooknation

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ClipDescription
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


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

    lateinit var filepath : Uri
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        database = Firebase.database.reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.s2_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnUpload = getView()?.findViewById<Button>(R.id.btnUpload)
        val btnSelect = getView()?.findViewById<Button>(R.id.btnSelectImage)

        if (btnSelect != null) {
            btnSelect.setOnClickListener{
                selectImage()
            }
        }

        if (btnUpload != null) {
            btnUpload.setOnClickListener{
                uploadRecipe()
            }
        }
    }

    private fun selectImage() {
        val i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i,"Select Image"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 100 && resultCode === Activity.RESULT_OK && data != null) {
            filepath = data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filepath)
            val iv = view?.findViewById<ImageView>(R.id.ivPreview)
            iv?.setImageBitmap(bitmap)
        }
    }

    data class Recipe(val recipeTitle: String? = null, val recipeDescription: String? = null){}

    fun addReciüe(inputTitle: String, inputDescription: String) {
        val recipe = Recipe(inputTitle, inputDescription)
        //TODO: replace Placeholder with userId ->
        database.child("Recipes").child("Placeholder").setValue(inputTitle)

    }

    private fun uploadRecipe() {
        val title = view?.findViewById<TextInputEditText>(R.id.etRecipeName).toString()
        val description = view?.findViewById<TextInputEditText>(R.id.etRecipeDescription).toString()
        if(filepath!=null && title!=null && description!=null) {

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
}

