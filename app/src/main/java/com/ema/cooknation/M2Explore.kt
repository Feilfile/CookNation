package com.ema.cooknation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.ema.cooknation.adapter.CardAdapter
import com.ema.cooknation.databinding.FragmentM2ExploreBinding
import com.ema.cooknation.model.Recipe
import com.ema.cooknation.model.recipeList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [M2Explore.newInstance] factory method to
 * create an instance of this fragment.
 */
class M2Explore : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }




    }

    private var _binding : FragmentM2ExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        populateRecipes()
        //val view = inflater.inflate(R.layout.fragment_m2_explore,container, false)


        _binding = FragmentM2ExploreBinding.inflate(inflater, container, false)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity?.applicationContext, 3)
            adapter = CardAdapter(recipeList)
        }

        //return inflater.inflate(R.layout.fragment_m2_explore, container, false)

        return binding.root
    }

    private fun populateRecipes() {
        val recipe1 = Recipe(
            R.drawable.cheeseburger,
            "Cheeseburger",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe1)

        val recipe2 = Recipe(
            R.drawable.bowl,
            "Soup",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe2)

        val recipe3 = Recipe(
            R.drawable.fruitbowl,
            "Fruitbowl",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe3)

        val recipe4 = Recipe(
            R.drawable.grilledcheese,
            "Grilled Cheese",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe4)

        val recipe5 = Recipe(
            R.drawable.macncheese,
            "Cheeseburger",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe5)

        val recipe6 = Recipe(
            R.drawable.pizza,
            "Pizza",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe6)

        val recipe7 = Recipe(
            R.drawable.rampen,
            "Ramen",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe7)

        val recipe8 = Recipe(
            R.drawable.cheeseburger,
            "Cheeseburger",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe8)

        val recipe9 = Recipe(
            R.drawable.bowl,
            "Soup",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe9)

        val recipe10 = Recipe(
            R.drawable.fruitbowl,
            "Fruitbowl",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe10)

        val recipe11 = Recipe(
            R.drawable.grilledcheese,
            "Grilled Cheese",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe11)

        val recipe12 = Recipe(
            R.drawable.macncheese,
            "Cheeseburger",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe12)

        val recipe13 = Recipe(
            R.drawable.pizza,
            "Pizza",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe13)

        val recipe14 = Recipe(
            R.drawable.rampen,
            "Ramen",
            "Leon Braun",
            "ABC 123"
        )
        recipeList.add(recipe14)

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
            M2Explore().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}