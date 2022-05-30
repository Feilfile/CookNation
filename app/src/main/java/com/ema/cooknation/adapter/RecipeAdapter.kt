package com.ema.cooknation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.CardViewHolder
import com.ema.cooknation.databinding.CardCellBinding
import com.ema.cooknation.model.Recipe

class RecipeAdapter(private val recipes: List<Recipe>)
    : RecyclerView.Adapter<CardViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindRecipe(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size
}