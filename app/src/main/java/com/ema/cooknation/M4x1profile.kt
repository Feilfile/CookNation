package com.ema.cooknation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [M4x1profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class M4x1profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeArrayList: ArrayList<Recipe>
    private lateinit var temprecipeArrayList: ArrayList<Recipe>
    private lateinit var cardAdapter: CardAdapter
    private lateinit var mAuth: FirebaseAuth

    private lateinit var btnSignOut: Button
    private lateinit var btnProfileUpload: Button
    private lateinit var tvProfileName: TextView

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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment m4_profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            M4x1profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpElements()
        setupRecyclerView()
    }

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
        temprecipeArrayList = arrayListOf()
        cardAdapter = CardAdapter(temprecipeArrayList)
        recyclerView.adapter = cardAdapter
        eventChangeListener()
    }

    private fun sortElementsByNewest(){
        val sortedList = temprecipeArrayList.sortedWith(compareByDescending {
            it.date
        })
        refreshAdapter(sortedList)
    }

    private fun refreshAdapter(sortedList: List<Recipe>){
        temprecipeArrayList.clear()
        temprecipeArrayList.addAll(sortedList)
        cardAdapter.notifyDataSetChanged()
    }

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
    }

    fun resetAdapter(){
        temprecipeArrayList.clear()
        eventChangeListener()
    }

}