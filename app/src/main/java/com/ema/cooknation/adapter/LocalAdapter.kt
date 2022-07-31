package com.ema.cooknation.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.ui.MainActivity
import com.ema.cooknation.R
import com.ema.cooknation.ui.RecipeViewActivityOffline
import com.ema.cooknation.data.LocalRecipe
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.text.DateFormat

class LocalAdapter: RecyclerView.Adapter<LocalAdapter.MyViewHolder>() {

    private var localRecipeList = emptyList<LocalRecipe>()

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var picture : ImageView = itemView.findViewById(R.id.ivWideCardRecipeImg)
        val title : TextView = itemView.findViewById(R.id.tvWideCardRecipeName)
        val author : TextView = itemView.findViewById(R.id.tvWideCardAuthor)
        val date : TextView = itemView.findViewById(R.id.tvWideCardDate)
        val difficulty : TextView = itemView.findViewById(R.id.tvWideCardDifficulty)
        val prepTime : TextView = itemView.findViewById(R.id.tvWideCardPrepTime)
        val numRating : TextView = itemView.findViewById(R.id.tvNumRatings)
        val rating : MaterialRatingBar = itemView.findViewById(R.id.mrbWideCardAvgRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wide_card_cell, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return localRecipeList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = localRecipeList[position]
        holder.title.text = currentItem.title
        holder.author.text = currentItem.author
        holder.date.text = DateFormat.getDateInstance().format(currentItem.date)
        holder.difficulty.text = currentItem.difficulty
        holder.prepTime.text = currentItem.prepTime
        holder.rating.rating = currentItem.avgRating
        holder.numRating.text = currentItem.ratingCount.toString()
        holder.picture.setImageBitmap(BitmapFactory.decodeByteArray(currentItem.picture, 0 , currentItem.picture.size))
        setupOnClickListener(holder, position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(localRecipe: List<LocalRecipe>){
        this.localRecipeList = localRecipe
        notifyDataSetChanged()
    }


    private fun setupOnClickListener(holder: MyViewHolder, position: Int){
        holder.itemView.findViewById<LinearLayout>(R.id.llWideCardCell).setOnClickListener { v ->
            v!!.context as AppCompatActivity
            val intent = Intent(v.context, RecipeViewActivityOffline::class.java)
            //parse recipe to the activity
            intent.putExtra("recipeId", localRecipeList[position].docId)
            v.context.startActivity(intent)
            //(v.context as MainActivity).overridePendingTransition(R.anim.zoom_in, R.anim.static_animation)
            (v.context as MainActivity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            //activity.supportFragmentManager.beginTransaction().replace(R.id.clScreenWindow, recipeView).commit()
        }
    }
}