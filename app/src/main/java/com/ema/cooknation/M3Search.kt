package com.ema.cooknation

//import androidx.appcompat.widget.SearchView
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.adapter.CardAdapter
import com.ema.cooknation.model.Recipe
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class M3Search : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeArrayList: ArrayList<Recipe>
    private lateinit var temprecipeArrayList: ArrayList<Recipe>
    private lateinit var cardAdapter: CardAdapter

    private lateinit var searchBarText: SearchView
    private lateinit var sorter: ImageButton

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
        val rootView = inflater.inflate(R.layout.fragment_m3_search, container, false)
        recyclerView = rootView.findViewById(R.id.rvSearchRecyclerView)
        searchBarText = rootView.findViewById(R.id.etSearchBar)
        sorter = rootView.findViewById(R.id.ibSearchPopUpButton)
        db = Firebase.firestore
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupRecyclerView()
        activateSearchBar()
        popupMenu()
    }

    private fun setupButtons() {
        sorter.setOnClickListener{
            temprecipeArrayList.clear()
            sortElementsByNewest()
        }
    }

    private fun setupRecyclerView() {
        // Recyclerview gets sets set up with 2 recipe cards per row
        val layoutManager = GridLayoutManager(this.context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recipeArrayList = arrayListOf()
        temprecipeArrayList = arrayListOf()
        cardAdapter = CardAdapter(temprecipeArrayList)
        recyclerView.adapter = cardAdapter
        eventChangeListener()
    }

    /*private fun eventChangeListener() {
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    recipeArrayList.add(document.toObject<>())
                }
            }
    }*/

    //Loads all Elements into the adapter and sorts it before they load
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
    }


    // sorting functions for popup menu
    private fun sortElementsByNewest(){
        val sortedList = temprecipeArrayList.sortedWith(compareByDescending {
            it.date
        })
        refreshAdapter(sortedList)
    }

    private fun sortElementsByRating(){
        val sortedList = temprecipeArrayList.sortedWith(compareByDescending {
            it.avgRating
        })
        refreshAdapter(sortedList)
    }

    private fun sortElementsDifficultyEasy(){
        val sortedList = temprecipeArrayList.sortedWith(compareBy( {it.difficulty != "Easy"}, {it.difficulty}))
        refreshAdapter(sortedList)
    }

    private fun sortElementsDifficultyNormal(){
        val sortedList = temprecipeArrayList.sortedWith(compareBy( {it.difficulty != "Normal"}, {it.difficulty}))
        refreshAdapter(sortedList)
    }

    private fun sortElementsDifficultyHard(){
        val sortedList = temprecipeArrayList.sortedWith(compareBy( {it.difficulty != "Hard"}, {it.difficulty}))
        refreshAdapter(sortedList)
    }

    private fun sortElementsByOldest(){
        val sortedList = temprecipeArrayList.sortedWith(compareBy {
            it.date
        })
        refreshAdapter(sortedList)
    }

    private fun sortElementsByTitle(){
        val sortedList = temprecipeArrayList.sortedWith(compareBy {
            it.title.toString().lowercase()
        })
        refreshAdapter(sortedList)
    }

    // TODO: muss noch kommentiert werden, kann nicht genau sagen was das ist - Leon
    private fun refreshAdapter(sortedList: List<Recipe>){
        temprecipeArrayList.clear()
        temprecipeArrayList.addAll(sortedList)
        cardAdapter.notifyDataSetChanged()
    }

    private fun activateSearchBar() {

        searchBarText.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                temprecipeArrayList.clear()
                //conversion to lowercase
                var searchBarText = newText!!.lowercase(Locale.getDefault())
                //deletion of whitespaces TODO: extract in separate Function
                searchBarText = searchBarText.replace("\\s".toRegex(), "")
                if (searchBarText.isNotEmpty()) {
                    //all tags get split after a ',' and separated in a new array called words
                    var words = searchBarText.split(",").toTypedArray()
                    words = removeDuplicates(words)
                    val lastItem = words.last()

                    recipeArrayList.forEach{
                        //checks if all tags are found inside the Recipe and so it adds it into the bew list
                        for(word in words) {
                            if (it.title?.replace("\\s".toRegex(), "")?.lowercase(Locale.getDefault())!!.contains(word)
                                || it.author?.replace("\\s".toRegex(), "")?.lowercase(Locale.getDefault())!!.contains(word)
                                || it.ingredients?.replace("\\s".toRegex(), "")?.lowercase(Locale.getDefault())!!.contains(word))
                            {
                                if(word == lastItem) {
                                    temprecipeArrayList.add(it)
                                }
                                continue
                            } else {
                                break
                            }
                        }
                    }
                    cardAdapter.notifyDataSetChanged()
                } else {
                    refreshAdapter(recipeArrayList)
                }

                return false
            }

        })
    }

    // sets up popupmenu-options for various ways to sort recipes
    private fun popupMenu() {
        val popupMenu = PopupMenu(activity as MainActivity, sorter)
        popupMenu.inflate(R.menu.search_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.iNewRecipe -> {
                    Toast.makeText(
                        activity as MainActivity,
                        "Sorting by newest recipes...",
                        Toast.LENGTH_SHORT
                    ).show()
                    sortElementsByNewest()
                    true
                }
                R.id.iTopRated -> {
                    Toast.makeText(
                        activity as MainActivity,
                        "Sorting by top rated recipes...",
                        Toast.LENGTH_SHORT
                    ).show()
                    sortElementsByRating()
                    true
                }
                R.id.iDifficulty -> {
                    true
                }
                R.id.iDifficulty1 -> {
                    Toast.makeText(
                        activity as MainActivity,
                        "Sorting by easy difficulty recipes...",
                        Toast.LENGTH_SHORT
                    ).show()
                    sortElementsDifficultyEasy()
                    true
                }
                R.id.iDifficulty2 -> {
                    Toast.makeText(
                        activity as MainActivity,
                        "Sorting by normal difficulty recipes...",
                        Toast.LENGTH_SHORT
                    ).show()
                    sortElementsDifficultyNormal()
                    true
                }
                R.id.iDifficulty3 -> {
                    Toast.makeText(
                        activity as MainActivity,
                        "Sorting by hard difficulty recipes...",
                        Toast.LENGTH_SHORT
                    ).show()
                    sortElementsDifficultyHard()
                    true
                }
                else -> true
            }
        }

        sorter.setOnClickListener {
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }
        }
    }

    fun removeDuplicates(array: Array<String>): Array<String>{
        return array.distinct().toTypedArray()
    }

    fun resetAdapter(){
        temprecipeArrayList.clear()
        eventChangeListener()
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            M3Search().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}