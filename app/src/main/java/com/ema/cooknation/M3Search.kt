package com.ema.cooknation

//import androidx.appcompat.widget.SearchView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.adapter.CardAdapter
import com.ema.cooknation.model.Recipe
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [M3Search.newInstance] factory method to
 * create an instance of this fragment.
 */
class M3Search : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeArrayList: ArrayList<Recipe>
    private lateinit var temprecipeArrayList: ArrayList<Recipe>
    private lateinit var cardAdapter: CardAdapter

    //Searching Elements
    private lateinit var searchBarText: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        //recyclerView = requireActivity().findViewById(R.id.rvSearchRecyclerView)
        val layoutManager = GridLayoutManager(this.context, 2)
        //layoutManager.orientation = GridLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        recipeArrayList = arrayListOf()
        temprecipeArrayList = arrayListOf()

        cardAdapter = CardAdapter(temprecipeArrayList)

        recyclerView.adapter = cardAdapter

        eventChangeListener()
    }

    private fun eventChangeListener() {
        db.collection("recipes")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            recipeArrayList.add(dc.document.toObject((Recipe::class.java)))
                        }
                    }
                    temprecipeArrayList.addAll(recipeArrayList)
                    cardAdapter.notifyDataSetChanged()
                    activateSearchBar()
                }
            })
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.searching_item,menu)
        val item = menu.findItem(R.id.iSearchAction)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                temprecipeArrayList.clear()
                val searchBarText = newText!!.lowercase(Locale.getDefault())
                if (searchBarText.isNotEmpty()) {

                    recipeArrayList.forEach{

                        if(it.title?.lowercase(Locale.getDefault())!!.contains(searchBarText)) {
                            temprecipeArrayList.add(it)
                        }
                    }
                    cardAdapter.notifyDataSetChanged()
                } else {
                    temprecipeArrayList.clear()
                    temprecipeArrayList.addAll(recipeArrayList)
                    cardAdapter.notifyDataSetChanged()
                }

                return false
            }

        })
    }*/

    private fun activateSearchBar() {

        searchBarText.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                temprecipeArrayList.clear()
                val searchBarText = newText!!.lowercase(Locale.getDefault())
                if (searchBarText.isNotEmpty()) {

                    recipeArrayList.forEach{

                        if(it.title?.lowercase(Locale.getDefault())!!.contains(searchBarText)) {
                            temprecipeArrayList.add(it)
                        } else if (it.author?.lowercase(Locale.getDefault())!!.contains(searchBarText)) {
                            temprecipeArrayList.add(it)
                        } else if (it.ingredients?.lowercase(Locale.getDefault())!!.contains(searchBarText)) {
                            temprecipeArrayList.add(it)
                        }
                    }
                    cardAdapter.notifyDataSetChanged()
                } else {
                    temprecipeArrayList.clear()
                    temprecipeArrayList.addAll(recipeArrayList)
                    cardAdapter.notifyDataSetChanged()
                }

                return false
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_m3_search, container, false)
        recyclerView = rootView.findViewById(R.id.rvSearchRecyclerView)
        searchBarText = rootView.findViewById(R.id.etSearchBar)
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment m2_explore.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            M3Search().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}