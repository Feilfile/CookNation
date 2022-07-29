package com.ema.cooknation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.ema.cooknation.R
import com.ema.cooknation.data.Recipe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class BottomSheetPopupDelete : BottomSheetDialogFragment() {

    private lateinit var cancelButton: Button
    private lateinit var confirmButton: Button
    private lateinit var recipe: Recipe
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.bottomsheet_popup_delete, container, false)
        cancelButton = rootView.findViewById(R.id.btnCancelDelete)
        confirmButton = rootView.findViewById(R.id.btnConfirmDelete)
        db = Firebase.firestore
        return rootView
    }

    // TODO: implement coroutines
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
                deleteFromAllFavorites()
            }
    }

    private fun deleteRecipe(){

        db.collection("recipes").document(recipe.docId.toString()).delete()
            .addOnSuccessListener {
                (activity as S1RecipeViewActivity).finish()
            }.addOnFailureListener{
                Log.e("deleteRecipe", "couldn't delete recipe")
            }
    }

    private fun deleteFromAllFavorites() {
        db.collection("favorites")
            .get()
            .addOnSuccessListener { usersIdList ->
                for (userId in usersIdList) {
                    db.collection("favorites")
                        .document(userId.id)
                        .collection("docId")
                        .document(recipe.docId.toString())
                        .delete()
                        .addOnSuccessListener {
                            deleteRecipe()
                        }
                }
            }
    }
}