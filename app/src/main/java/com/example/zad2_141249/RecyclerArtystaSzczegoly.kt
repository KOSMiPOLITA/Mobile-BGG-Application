package com.example.zad2_141249

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.edit_game_item2.view.*

class RecyclerArtystaSzczegoly(private val exampleList: ArrayList<String>?) :
    RecyclerView.Adapter<RecyclerArtystaSzczegoly.ArtDetViewHolder>() {

    inner class ArtDetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textV: TextView = itemView.dodawanieArtystaText
        val buttonV: ImageButton = itemView.usuwanieArtystaButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtDetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_game_item2, parent, false)
        return ArtDetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArtDetViewHolder, position: Int) {
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