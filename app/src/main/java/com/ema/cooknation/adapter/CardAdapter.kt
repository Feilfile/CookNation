package com.ema.cooknation.adapter

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ema.cooknation.R
import com.ema.cooknation.model.Recipe
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class CardAdapter(private val recipes: ArrayList<Recipe>)
    : RecyclerView.Adapter<CardAdapter.ViewHolder>()
{

    //Referenzierung auf card_cell.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_cell, parent, false)
        return ViewHolder(itemView)
    }

    // Aktuelle Karte wird im Viewholder geladen bzw. aktualisiert
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe : Recipe = recipes[position]
        holder.title.text = recipe.title
        //drop 1 to prevent a double "/"
        val storageRef = FirebaseStorage.getInstance().getReference(recipe.picturePath.toString().drop(1))
        var localfile = File.createTempFile("tempfile", ".jpg")
        storageRef.getFile(localfile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                holder.picture.setImageBitmap(bitmap)
            }.addOnFailureListener{
            }
    }

    //Referenzierung auf die Elemente (TextView, ImageView etc.) wird aufgebaut
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.tvRecipeName)
        var picture : ImageView = itemView.findViewById(R.id.ivRecipeImg)
    }

    override fun getItemCount(): Int = recipes.size
}
