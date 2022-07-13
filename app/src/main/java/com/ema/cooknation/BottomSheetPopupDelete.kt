package com.ema.cooknation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.ema.cooknation.model.Recipe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class BottomSheetPopupDelete : BottomSheetDialogFragment() {

    private lateinit var cancelButton: Button
    private lateinit var confirmButton: Button
    private lateinit var recipe: Recipe

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.bottomsheet_popup_delete, container, false)
        cancelButton = rootView.findViewById(R.id.btnCancelDelete)
        confirmButton = rootView.findViewById(R.id.btnConfirmDelete)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipe = (activity as S1RecipeViewActivity).getRecipe()
        cancelButton.setOnClickListener {
            dismiss()
        }

        confirmButton.setOnClickListener {
            deletePicture()
        }
    }

    private fun deletePicture(){
        Log.d("deletePic", "Recipes/${recipe.uid.toString()}/${recipe.docId}".drop(1))
        FirebaseStorage.getInstance().getReference("Recipes/${recipe.uid.toString()}/${recipe.docId}").delete()
            .addOnSuccessListener{
                deleteRecipe()
            }
    }

    private fun deleteRecipe(){
        FirebaseFirestore.getInstance().collection("recipes").document(recipe.docId.toString()).delete()
            .addOnSuccessListener {
                (activity as S1RecipeViewActivity).finish()
            }.addOnFailureListener{
                Log.e("deleteRecipe", "couldn't delete recipe")
            }
    }
}