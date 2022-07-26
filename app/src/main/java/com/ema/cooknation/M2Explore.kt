package com.ema.cooknation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.adapter.CardAdapter
import com.ema.cooknation.adapter.WideCardAdapter
import com.ema.cooknation.model.Recipe
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class M2Explore : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeArrayList: ArrayList<Recipe>

    private lateinit var cardAdapter: WideCardAdapter

    private lateinit var ibButtonWebView: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_m2_explore, container, false)
        ibButtonWebView = rootView.findViewById(R.id.ibButtonWebView)
        recyclerView = rootView.findViewById(R.id.exploreRecyclerView)
        db = Firebase.firestore
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = GridLayoutManager(this.context, 1)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(15)

        recipeArrayList = arrayListOf()

        cardAdapter = WideCardAdapter(recipeArrayList)

        recyclerView.adapter = cardAdapter

        eventChangeListener()

        // Button to go to WebView
        ibButtonWebView.setOnClickListener {
            (activity as MainActivity).openWebViewActivity()
        }
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

                    cardAdapter.notifyDataSetChanged()
                }
            })
    }

    /*private var _binding : FragmentM2ExploreBinding? = null
    private val binding get() = _binding!!*/

    companion object {
        fun newInstance() = M2Explore().apply {}
    }
}