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
    private lateinit var tempRecipeArrayList: ArrayList<Recipe>
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeElements(view)
        setupRecyclerView()
        activateSearchBar()
        popupMenu()
    }

    private fun popupMenu() {
        val popupMenu = PopupMenu(activity as MainActivity,sorter)
        popupMenu.inflate(R.menu.search_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.iNewRecipe -> {
                    Toast.makeText(activity as MainActivity, "Sorting by newest recipes...", Toast.LENGTH_SHORT).show()
                    sortElementsByNewest()
                    true
                }
                R.id.iTopRated -> {
                Toast.makeText(activity as MainActivity, "Sorting by top rated recipes...", Toast.LENGTH_SHORT).show()
                    sortElementsByTitle()
                true
                }
                R.id.iDifficulty -> {
                    Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.iDifficulty1 -> {
                    Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.iDifficulty2 -> {
                    Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.iDifficulty3 -> {
                    Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
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
                .getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }
        }
    }

    private fun initializeElements(view : View) {
        db = Firebase.firestore
        sorter = view.findViewById(R.id.ibSearchPopUpButton)
        /*sorter.setOnClickListener{
            sortElementsByNewest()
        }*/
    }

    private fun setupRecyclerView() {
        // 2 cards per row
        val layoutManager = GridLayoutManager(this.context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recipeArrayList = arrayListOf()
        tempRecipeArrayList = arrayListOf()
        cardAdapter = CardAdapter(tempRecipeArrayList)
        recyclerView.adapter = cardAdapter
        eventChangeListener()
    }

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
                    sortElementsByTitle()
                }
            })
    }
/*
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity.menuInflater(R.menu.search_menu,menu)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.iNewRecipe -> Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
            R.id.iTopRated -> Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
            R.id.iDifficulty -> Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
            R.id.iDifficulty1 -> Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
            R.id.iDifficulty2 -> Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
            R.id.iDifficulty3 -> Toast.makeText(activity as MainActivity, "Sorting by newest recipe...", Toast.LENGTH_SHORT).show()
        }
        return super.onContextItemSelected(item)
    }
*/



    private fun sortElementsByTitle(){
        val sortedList = tempRecipeArrayList.sortedWith(compareBy {
            it.title.toString().lowercase()
        })
        refreshAdapter(sortedList)
    }

    private fun sortElementsByNewest(){
        val sortedList = tempRecipeArrayList.sortedWith(compareByDescending {
            it.date
        })
        refreshAdapter(sortedList)
    }

    private fun sortElementsByOldest(){
        val sortedList = tempRecipeArrayList.sortedWith(compareBy {
            it.date
        })
        refreshAdapter(sortedList)
    }

    //TODO: private fun SortByRating(){} and other sorting possibilities

    private fun refreshAdapter(sortedList: List<Recipe>){
        tempRecipeArrayList.clear()
        tempRecipeArrayList.addAll(sortedList)
        cardAdapter.notifyDataSetChanged()
    }

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

    fun removeDuplicates(array: Array<String>): Array<String>{
        return array.distinct().toTypedArray()
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