package com.ema.cooknation.adapter

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
import com.ema.cooknation.R
import com.ema.cooknation.S1RecipeViewActivity
import com.ema.cooknation.model.Recipe
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.Serializable


class CardAdapter(private val recipes: ArrayList<Recipe>)
    : RecyclerView.Adapter<CardAdapter.ViewHolder>()
{

    //loads card_cell
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_cell, parent, false)
        return ViewHolder(itemView)
    }

    // current card gets loaded / updated on the right position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe : Recipe = recipes[position]
        holder.title.text = recipe.title
        holder.author.text = recipe.author
        loadPictureInContainer(recipe, holder)
        setupOnClickListener(holder, position)
    }

    //references the elements inside the card_cell
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.tvRecipeName)
        val author : TextView = itemView.findViewById(R.id.tvAuthor)
        var picture : ImageView = itemView.findViewById(R.id.ivRecipeImg)
    }

    override fun getItemCount(): Int = recipes.size

    private fun loadPictureInContainer (recipe: Recipe, holder: ViewHolder) {
        //drop 1 to prevent a double "/"
        val storageRef = FirebaseStorage.getInstance().getReference(recipe.picturePath.toString().drop(1))
        val localFile = File.createTempFile("tempFile", ".jpg")
        storageRef.getFile(localFile)
            .addOnSuccessListener {
                var bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.picture.setImageBitmap(bitmap)
            }.addOnFailureListener{
                //TODO: implement OnFailureListener in loadPictureInContainer
            }
    }

    private fun setupOnClickListener(holder: ViewHolder, position: Int){
        holder.itemView.findViewById<LinearLayout>(R.id.llCardCell).setOnClickListener { v ->
            v!!.context as AppCompatActivity
            val intent = Intent(v.context, S1RecipeViewActivity::class.java)
            //parse recipe to the activity
            intent.putExtra("recipe", recipes[position] as Serializable)
            v.context.startActivity(intent)
            //activity.supportFragmentManager.beginTransaction().replace(R.id.clScreenWindow, recipeView).commit()
        }
    }
}
