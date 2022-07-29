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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipe = (activity as RecipeViewActivity).getRecipe()
        cancelButton.setOnClickListener {
            dismiss()
        }

        confirmButton.setOnClickListener {
            runBlocking {
                deletePicture()
                deleteRecipe()
                (activity as RecipeViewActivity).finish()
            }
        }
    }

    private suspend fun deletePicture(){
        FirebaseStorage.getInstance().getReference("Recipes/${recipe.uid.toString()}/${recipe.docId}").delete()
            .await()
    }

    private suspend fun deleteRecipe() {
        db.collection("recipes").document(recipe.docId.toString()).delete()
            .addOnSuccessListener {
            }.addOnFailureListener{
                Log.e("deleteRecipe", "couldn't delete recipe")
            }.await()
    }

    /* CONSTRUCTION DOESN'T WORK AND WOULD BE SLOW
    private suspend fun deleteFromAllFavorites() {

        val usersIdList = db.collectionGroup("user")
            .get()
            .await()
        for (userid in usersIdList.documents) {
            Log.e("Test", userid.id)
        }
        /*for (userId in usersIdList) {
            db.collection("favorites")
                .document(userId.id)
                .collection("docId")
                .document(recipe.docId.toString())
                .delete()
                .await()
            }*/
    }*/
}