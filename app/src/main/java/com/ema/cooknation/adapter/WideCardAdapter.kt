package com.ema.cooknation.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
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
import com.ema.cooknation.ui.RecipeViewActivity
import com.ema.cooknation.data.Recipe
import com.google.firebase.storage.FirebaseStorage
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.io.File
import java.text.DateFormat

class WideCardAdapter(private val recipes: ArrayList<Recipe>)
    : RecyclerView.Adapter<WideCardAdapter.ViewHolder>()
{
    /**
     * loads card_cell
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wide_card_cell, parent, false)
        return ViewHolder(itemView)
    }

    /**
     * current card gets loaded / updated on the right position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe : Recipe = recipes[position]

        holder.title.text = recipe.title
        holder.author.text = recipe.author
        holder.date.text = DateFormat.getDateInstance().format(recipe.date)
        holder.difficulty.text = recipe.difficulty
        holder.prepTime.text = recipe.prepTime
        holder.rating.rating = recipe.avgRating
        holder.numRating.text = recipe.ratingCount.toString()
        loadPictureInContainer(recipe, holder)
        setupOnClickListener(holder, position)
    }

    /**
     * references the elements inside the card_cell
     */
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var picture : ImageView = itemView.findViewById(R.id.ivWideCardRecipeImg)
        val title : TextView = itemView.findViewById(R.id.tvWideCardRecipeName)
        val author : TextView = itemView.findViewById(R.id.tvWideCardAuthor)
        val date : TextView = itemView.findViewById(R.id.tvWideCardDate)
        val difficulty : TextView = itemView.findViewById(R.id.tvWideCardDifficulty)
        val prepTime : TextView = itemView.findViewById(R.id.tvWideCardPrepTime)
        val numRating : TextView = itemView.findViewById(R.id.tvNumRatings)
        val rating : MaterialRatingBar = itemView.findViewById(R.id.mrbWideCardAvgRating)
    }

    override fun getItemCount(): Int = recipes.size

    private fun loadPictureInContainer (recipe: Recipe, holder: ViewHolder) {
        //drop 1 to prevent a double "/"
        val storageRef = FirebaseStorage.getInstance().getReference(recipe.picturePath.toString().drop(1))
        val localFile = File.createTempFile("tempFile", ".jpg")
        storageRef.getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.picture.setImageBitmap(bitmap)
            }.addOnFailureListener{
                Log.e("pictureQuery", "error while loading picture from Firestore storage")
            }
    }

    private fun setupOnClickListener(holder: ViewHolder, position: Int){
        holder.itemView.findViewById<LinearLayout>(R.id.llWideCardCell).setOnClickListener { v ->
            v!!.context as AppCompatActivity
            val intent = Intent(v.context, RecipeViewActivity::class.java)
            //parse recipe to the activity
            intent.putExtra("recipeId", recipes[position].docId)
            v.context.startActivity(intent)
            (v.context as MainActivity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            //activity.supportFragmentManager.beginTransaction().replace(R.id.clScreenWindow, recipeView).commit()
        }
    }
}