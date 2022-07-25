package com.ema.cooknation

//import java.util.ArrayList
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.adapter.LocalAdapter
import com.ema.cooknation.adapter.WideCardAdapter
import com.ema.cooknation.data.LocalRecipe
import com.ema.cooknation.model.Recipe
import com.ema.cooknation.viewmodel.LocalRecipeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class M1Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private lateinit var localRecipeViewModel: LocalRecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_m1_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = requireActivity().findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this.context, 1)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(15)
        localRecipeViewModel = ViewModelProvider(this)[LocalRecipeViewModel::class.java]




        if(isOnline(requireContext())){
            val recipeArrayList = arrayListOf<Recipe>()
            val cardAdapter = WideCardAdapter(recipeArrayList)
            recyclerView.adapter = cardAdapter
            db = Firebase.firestore
            mAuth = FirebaseAuth.getInstance()
            eventChangeListener(recipeArrayList, cardAdapter)
        } else {
            arrayListOf<LocalRecipe>()
            val cardAdapter = LocalAdapter()
            recyclerView.adapter = cardAdapter

            localRecipeViewModel.readAllData.observe(viewLifecycleOwner) { localRecipe ->
                cardAdapter.setData(localRecipe)
            }
        }
    }

    private fun eventChangeListener(
        recipeArrayList: ArrayList<Recipe>,
        cardAdapter: WideCardAdapter
    ) {

        val favoritesList: MutableList<String> = arrayListOf()
        runBlocking {
        val resultFavourites = db.collection("favorites")
            .document(mAuth.uid.toString())
            .collection("docId")
            .get()
            .await()
            for (document in resultFavourites) {
                favoritesList.add(document.id)
            }
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

    //Source https://stackoverflow.com/questions/51141970/check-internet-connectivity-android-in-kotlin
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            M1Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}