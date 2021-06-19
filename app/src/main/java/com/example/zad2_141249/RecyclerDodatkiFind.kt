package com.example.zad2_141249

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dodatki_item.view.*

class RecyclerDodatkiFind(private val exampleList: ArrayList<DodatkiDoGry>?) :
    RecyclerView.Adapter<RecyclerDodatkiFind.DodatkiFindViewHolder>() {

        inner class DodatkiFindViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val dodatekName: TextView = itemView.dodatek_tytul_find
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DodatkiFindViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.dodatki_item, parent, false)
        return DodatkiFindViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DodatkiFindViewHolder, position: Int) {
        val currentItem = exampleList?.get(position)
        if (currentItem != null) {
            holder.dodatekName.text = currentItem.tytul
        }
    }

    override fun getItemCount(): Int {
        if (exampleList != null) {
            return exampleList.size
        }
        return -1
    }

}