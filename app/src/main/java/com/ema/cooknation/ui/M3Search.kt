package com.ema.cooknation.ui

//import androidx.appcompat.widget.SearchView
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.R
import com.ema.cooknation.adapter.CardAdapter
import com.ema.cooknation.data.Recipe
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*

class M3Search : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    //recipeArrayList has all Elements in the database
    //tempRecipeArrayList has all Elements that are loaded inside the recycler view and gets changed after searching/sorting
    private lateinit var recipeArrayList: ArrayList<Recipe>
    private lateinit var tempRecipeArrayList: ArrayList<Recipe>
    private lateinit var cardAdapter: CardAdapter

    private lateinit var searchBarText: SearchView
    private lateinit var sorter: ImageButton

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
            tempRecipeArrayList.clear()
            sortElementsByNewest()
        }
    }

    private fun setupRecyclerView() {
        // Recyclerview gets set up with 2 recipe cards per row
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

    private fun loadAdapter() {
        recipeArrayList.clear()
        runBlocking (Dispatchers.IO){
            //generate all objects to the list and load it into the CardAdapter
            val recipes = db.collection("recipes")
                .get()
                .await()
            for (foundRecipe in recipes.documents) {
                recipeArrayList.add(foundRecipe.toObject(Recipe::class.java)!!)
            }
            tempRecipeArrayList.addAll(recipeArrayList)
            sortElementsByNewest()
        }
    }

/*  LEGACY ADAPTER
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
                    tempRecipeArrayList.addAll(recipeArrayList)
                    sortElementsByNewest()
                }
            })
    }*/

    private fun activateSearchBar() {

        searchBarText.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempRecipeArrayList.clear()
                //conversion to lowercase
                var searchBarText = newText!!.lowercase(Locale.getDefault())
                //deletion of whitespaces TODO: extract in separate Function
                searchBarText = searchBarText.replace("\\s".toRegex(), "")
                if (searchBarText.isNotEmpty()) {
                    //all tags get split after a ',' and separated in a new array called words
                    var arguments = searchBarText.split(",").toTypedArray()
                    arguments = removeDuplicates(arguments)
                    val lastItem = arguments.last()

                    recipeArrayList.forEach{
                        //checks if all tags are found inside the Recipe and so it adds it into the bew list
                        for(argument in arguments) {
                            if (it.title?.replace("\\s".toRegex(), "")?.lowercase(Locale.getDefault())!!.contains(argument)
                                || it.author?.replace("\\s".toRegex(), "")?.lowercase(Locale.getDefault())!!.contains(argument)
                                || it.ingredients?.replace("\\s".toRegex(), "")?.lowercase(Locale.getDefault())!!.contains(argument))
                            {
                                //when the last element fulfills the if condition the recipe is valid and gets added to the new list
                                if(argument == lastItem) {
                                    tempRecipeArrayList.add(it)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
    }

    /* sorting functions for popup menu */

    private fun sortElementsByNewest(){
        val sortedList = tempRecipeArrayList.sortedWith(compareByDescending {
            it.date
        })
        refreshAdapter(sortedList)
    }

    private fun sortElementsByRating(){
        val sortedList = tempRecipeArrayList.sortedWith(compareByDescending {
            it.avgRating
        })
        refreshAdapter(sortedList)
    }

    private fun sortElementsDifficultyEasy(){
        val sortedList = tempRecipeArrayList.sortedWith(compareBy( {it.difficulty != "Easy"}, {it.difficulty}))
        refreshAdapter(sortedList)
    }

    private fun sortElementsDifficultyNormal(){
        val sortedList = tempRecipeArrayList.sortedWith(compareBy( {it.difficulty != "Normal"}, {it.difficulty}))
        refreshAdapter(sortedList)
    }

    private fun sortElementsDifficultyHard(){
        val sortedList = tempRecipeArrayList.sortedWith(compareBy( {it.difficulty != "Hard"}, {it.difficulty}))
        refreshAdapter(sortedList)
    }

    /*  USELESS Queries
        private fun sortElementsByOldest(){
            val sortedList = tempRecipeArrayList.sortedWith(compareBy {
                it.date
            })
            refreshAdapter(sortedList)
        }
        private fun sortElementsByTitle(){
            val sortedList = tempRecipeArrayList.sortedWith(compareBy {
                it.title.toString().lowercase()
            })
            refreshAdapter(sortedList)
        }
    */

    // current output list gets cleared and refilled with the sorted one
    private fun refreshAdapter(sortedList: List<Recipe>){
        tempRecipeArrayList.clear()
        tempRecipeArrayList.addAll(sortedList)
        cardAdapter.notifyDataSetChanged()
    }

    private fun removeDuplicates(array: Array<String>): Array<String>{
        return array.distinct().toTypedArray()
    }

    companion object {
        fun newInstance() = M3Search().apply {}
    }
}