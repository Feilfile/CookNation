package com.ema.cooknation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.adapter.LocalAdapter
import com.ema.cooknation.adapter.WideCardAdapter
import com.ema.cooknation.data.LocalRecipe
import com.ema.cooknation.viewmodel.LocalRecipeViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [M1Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class M1Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeArrayList: ArrayList<LocalRecipe>

    private lateinit var cardAdapter: LocalAdapter
    private lateinit var localRecipeViewModel: LocalRecipeViewModel

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
        val rootView = inflater.inflate(R.layout.fragment_m1_home, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = requireActivity().findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this.context, 1)
        recyclerView.setHasFixedSize(true)

        recipeArrayList = arrayListOf()

        cardAdapter = LocalAdapter()

        recyclerView.adapter = cardAdapter

        localRecipeViewModel = ViewModelProvider(this).get(LocalRecipeViewModel::class.java)
        localRecipeViewModel.readAllData.observe(viewLifecycleOwner, Observer { localRecipe ->
            cardAdapter.setData(localRecipe)
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment m1_home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            M1Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}