package com.example.zad2_141249

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.edit_game_item.view.*

class RecyclerProjektantDodawanie(private val exampleList: ArrayList<String>?) :
    RecyclerView.Adapter<RecyclerProjektantDodawanie.ProjDodViewHolder>() {

    class ProjDodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textV: TextView = itemView.dodawanieProjektantText
        val buttonV: ImageButton = itemView.usuwanieProjektantButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjDodViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_game_item, parent, false)
        return ProjDodViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProjDodViewHolder, position: Int) {
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