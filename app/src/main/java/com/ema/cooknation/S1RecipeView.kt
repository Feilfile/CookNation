package com.ema.cooknation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ema.cooknation.databinding.CardCellBinding
import com.ema.cooknation.databinding.FragmentM2ExploreBinding
import com.ema.cooknation.databinding.S1RecipeviewBinding
import com.ema.cooknation.model.RECIPE_ID_EXTRA
import com.ema.cooknation.model.Recipe
import com.ema.cooknation.model.recipeList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [S1RecipeView.newInstance] factory method to
 * create an instance of this fragment.
 */
class S1RecipeView : Fragment() {
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

    private var _binding: S1RecipeviewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = S1RecipeviewBinding.inflate(inflater, container, false)
        val recipeID = activity?.intent?.getIntExtra(RECIPE_ID_EXTRA, -1)
        val recipe = recipeFromID(recipeID)
        if(recipe != null) {
            binding.recipeImg.setImageResource(recipe.recipeImg)
            binding.recipeName.text = recipe.recipeName
            binding.author.text = recipe.author
            binding.ingredients.text = recipe.ingredients
            binding.directions.text = recipe.directions

        }
        return binding.root
    }

    private fun recipeFromID(recipeID: Int?): Recipe? {
        for(recipe in recipeList) {
            if(recipe.id == recipeID)
                return recipe
        }
        return null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment S1RecipeView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            S1RecipeView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}