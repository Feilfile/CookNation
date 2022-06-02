package com.ema.cooknation

import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.databinding.CardCellBinding
import com.ema.cooknation.model.Recipe

class CardViewHolder(
    private val cardCellBinding: CardCellBinding,
    private val clickListener: RecipeClickListener
) : RecyclerView.ViewHolder(cardCellBinding.root) {

    fun bindRecipe(recipe: Recipe) {
        cardCellBinding.recipeImg.setImageResource(recipe.recipeImg)
        cardCellBinding.recipeName.text = recipe.recipeName
        cardCellBinding.author.text = recipe.author

        cardCellBinding.cardView.setOnClickListener {
            clickListener.onClick(recipe)
        }
    }
}