package com.ema.cooknation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.adapter.LocalAdapter
import com.ema.cooknation.adapter.WideCardAdapter
import com.ema.cooknation.model.Recipe
import com.ema.cooknation.viewmodel.LocalRecipeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class M1Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var localRecipeViewModel: LocalRecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_m1_home, container, false)
        recyclerView = rootView.findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this.context, 1)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(15)
        localRecipeViewModel = ViewModelProvider(this)[LocalRecipeViewModel::class.java]
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isInternetAvailable(requireContext())){
            loadOnlineCards()
        } else {
            loadOfflineCards()
        }
    }


    private fun loadOnlineCards() {
        //initialize elements for online CardAdapter
        val db = Firebase.firestore
        val mAuth = FirebaseAuth.getInstance()
        if (mAuth.uid == null) {
            loadOfflineCards()
            return
        }
        val recipeArrayList = arrayListOf<Recipe>()
        val cardAdapter = WideCardAdapter(recipeArrayList)
        recyclerView.adapter = cardAdapter
        val favoritesList: MutableList<String> = arrayListOf()
        //Database queries on background thread IO
        runBlocking (Dispatchers.IO){
            //Load favorites in from Firestore collection into list
            val resultFavourites = db.collection("favorites")
                .document(mAuth.uid.toString())
                .collection("docId")
                .get()
                .await()
            for (document in resultFavourites) {
                favoritesList.add(document.id)
            }
            //generate all objects of the list and load it into the CardAdapter
            for (favorite in favoritesList) {
                val favoriteRecipe = db.collection("recipes")
                    .document(favorite)
                    .get()
                    .await()
                recipeArrayList.add(favoriteRecipe.toObject(Recipe::class.java)!!)
            }
        cardAdapter.notifyDataSetChanged()
        }
    }

    private fun loadOfflineCards() {
        val cardAdapter = LocalAdapter()
        recyclerView.adapter = cardAdapter
        //load all locally saved Object inside the Room database
        localRecipeViewModel.readAllData.observe(viewLifecycleOwner) { localRecipe ->
            cardAdapter.setData(localRecipe)
        }
    }

    //Checks if user is online -> reused code
    //Source: https://stackoverflow.com/questions/53532406/activenetworkinfo-type-is-deprecated-in-api-level-28
    private fun isInternetAvailable(context: Context): Boolean {
        val result: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result
    }

    companion object {
        fun newInstance() = M1Home().apply {}
    }
}