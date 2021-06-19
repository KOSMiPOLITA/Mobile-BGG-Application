package com.example.zad2_141249

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.edit_game_item2.view.*

class RecyclerArtystaDodawanie(private val exampleList: ArrayList<String>?) :
    RecyclerView.Adapter<RecyclerArtystaDodawanie.ArtDodViewHolder>() {

    class ArtDodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textV: TextView = itemView.dodawanieArtystaText
        val buttonV: ImageButton = itemView.usuwanieArtystaButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtDodViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_game_item2, parent, false)
        return ArtDodViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArtDodViewHolder, position: Int) {
        val currentItem = exampleList?.get(position)
        holder.textV.text = currentItem
        holder.buttonV.setOnClickListener{
            exampleList?.removeAt(position)
            Log.i("pomocy-141249-us", "$currentItem")
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        if (exampleList != null) {
            return exampleList.size
        }
        return -1
    }

}