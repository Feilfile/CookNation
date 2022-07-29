package com.ema.cooknation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.R
import com.ema.cooknation.adapter.WideCardAdapter
import com.ema.cooknation.data.Recipe
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ExploreFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerViewWeekly: RecyclerView
    private lateinit var recyclerViewDaily: RecyclerView
    private lateinit var recipeArrayListDaily: ArrayList<Recipe>
    private lateinit var recipeArrayListWeekly: ArrayList<Recipe>
    private lateinit var cardAdapterDaily: WideCardAdapter
    private lateinit var cardAdapterWeekly: WideCardAdapter
    private lateinit var ibButtonWebView: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_explore, container, false)
        ibButtonWebView = rootView.findViewById(R.id.ibButtonWebView)
        recyclerViewDaily = rootView.findViewById(R.id.exploreRecyclerViewDaily)
        recyclerViewWeekly = rootView.findViewById(R.id.exploreRecyclerViewWeekly)
        db = Firebase.firestore
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        // Button to go to WebView
        ibButtonWebView.setOnClickListener {
            (activity as MainActivity).openWebViewActivity()
        }
    }

    private fun setupRecycler() {
        recyclerViewDaily.layoutManager = GridLayoutManager(this.context, 1)
        recyclerViewDaily.setHasFixedSize(true)
        recyclerViewDaily.isNestedScrollingEnabled = false
        recipeArrayListDaily = arrayListOf()
        cardAdapterDaily = WideCardAdapter(recipeArrayListDaily)
        recyclerViewDaily.adapter = cardAdapterDaily

        recyclerViewWeekly.layoutManager = GridLayoutManager(this.context, 1)
        recyclerViewWeekly.setHasFixedSize(true)
        recyclerViewWeekly.setItemViewCacheSize(15)
        recipeArrayListWeekly = arrayListOf()
        cardAdapterWeekly = WideCardAdapter(recipeArrayListWeekly)
        recyclerViewWeekly.adapter = cardAdapterWeekly

        loadAdapter()
    }

    private fun loadAdapter() {
        recipeArrayListDaily.clear()
        runBlocking (Dispatchers.IO) {
            val recipes = db.collection("recipes")
                .whereEqualTo("docId", "6rXhMcMKqkimsh1S9E7Y")
                .get()
                .await()
            for (foundDailyRecipe in recipes.documents) {
                recipeArrayListDaily.add(foundDailyRecipe.toObject(Recipe::class.java)!!)
            }
        }
        recipeArrayListWeekly.clear()
        runBlocking (Dispatchers.IO){
            //generate all objects where uid == admin account
            val recipes = db.collection("recipes")
                .whereEqualTo("uid", "5ugEQTCc2qd9gIAAfBQeQyL34aq2")
                .get()
                .await()
            for (foundRecipe in recipes.documents) {
                recipeArrayListWeekly.add(foundRecipe.toObject(Recipe::class.java)!!)
            }
            sortListByNewest()
        }
    }

    private fun sortListByNewest() {
        val sortedList = recipeArrayListWeekly.sortedWith(compareByDescending {
            it.date
        })
        recipeArrayListWeekly.clear()
        recipeArrayListWeekly.addAll(sortedList)
        cardAdapterWeekly.notifyDataSetChanged()
    }

    companion object {
        fun newInstance() = ExploreFragment().apply {}
    }
}