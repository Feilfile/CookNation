package com.ema.cooknation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.adapter.CardAdapter
import com.ema.cooknation.model.Recipe
import com.ema.cooknation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class M4x1profile : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeArrayList: ArrayList<Recipe>
    private lateinit var tempRecipeArrayList: ArrayList<Recipe>
    private lateinit var cardAdapter: CardAdapter
    private lateinit var mAuth: FirebaseAuth

    private lateinit var btnSignOut: Button
    private lateinit var btnProfileUpload: Button
    private lateinit var tvProfileName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_m4_profile, container, false)
        recyclerView = rootView.findViewById(R.id.rvProfileRecyclerView)
        btnSignOut = rootView.findViewById(R.id.btnSignOut)
        btnProfileUpload = rootView.findViewById(R.id.btnProfileUpload)
        tvProfileName = rootView.findViewById(R.id.tvProfileName)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        return rootView
    }

    companion object {
        fun newInstance() = M4x1profile().apply {}
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpElements()
        setupRecyclerView()
    }
    // TODO: Rebuild nach upload
    private fun setUpElements() {
        //dc.document.toObject((Recipe::class.java))
        db.collection("user")
            .document(mAuth.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject<User>()
                if (user != null) {
                    tvProfileName.text = user.username
                }
            }

        btnProfileUpload.setOnClickListener{
            (activity as MainActivity).openUploadFragment()
        }
        // Setup SignOut Button when Loading Fragment
        btnSignOut.setOnClickListener{
            mAuth.signOut()
            (activity as MainActivity).performLogout()
        }
    }

    private fun setupRecyclerView() {
        // 2 cards per row
        val layoutManager = GridLayoutManager(this.context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(15)
        recipeArrayList = arrayListOf()
        tempRecipeArrayList = arrayListOf()
        cardAdapter = CardAdapter(tempRecipeArrayList)
        recyclerView.adapter = cardAdapter
        loadAdapter()
    }

    private fun sortElementsByNewest(){
        val sortedList = tempRecipeArrayList.sortedWith(compareByDescending {
            it.date
        })
        refreshAdapter(sortedList)
    }

    private fun refreshAdapter(sortedList: List<Recipe>){
        tempRecipeArrayList.clear()
        tempRecipeArrayList.addAll(sortedList)
        cardAdapter.notifyDataSetChanged()
    }

    //TODO: ADVANCED: Refresh specific item after returning from recipeview with notifyItemChanged(updateIndex)
    private fun loadAdapter() {
        recipeArrayList.clear()
        runBlocking (Dispatchers.IO){
            //generate all objects where uid == mAuth.uid of the list and load it into the CardAdapter
            val recipes = db.collection("recipes")
                .whereEqualTo("uid", mAuth.uid)
                .get()
                .await()
            for (foundRecipe in recipes.documents) {
                recipeArrayList.add(foundRecipe.toObject(Recipe::class.java)!!)
            }
            tempRecipeArrayList.addAll(recipeArrayList)
            sortElementsByNewest()
        }
    }

    /* LEGACY ADAPTER
    private fun eventChangeListener() {
        db.collection("recipes")
            .whereEqualTo("uid", mAuth.uid)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    //recipeArray needs to be cleared otherwise the Dataset will be doubled after changing a recipe
                    recipeArrayList.clear()

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            recipeArrayList.add(dc.document.toObject((Recipe::class.java)))
                        }
                    }
                    temprecipeArrayList.addAll(recipeArrayList)
                    sortElementsByNewest()
                }
            })
    }*/

}