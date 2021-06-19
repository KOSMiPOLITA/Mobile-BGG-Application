package com.example.zad2_141249

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dodatki_item.view.*

class RecyclerGryZLokacji(private val exampleList: ArrayList<String>?) :
    RecyclerView.Adapter<RecyclerGryZLokacji.LokGryViewHolder>() {

    class LokGryViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        val tytul = itemView.dodatek_tytul_find
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LokGryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.dodatki_item, parent, false)
        return LokGryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LokGryViewHolder, position: Int) {
        val currentItem = exampleList?.get(position)
        if (currentItem != null) {
            holder.tytul.text = currentItem
        }
    }

    override fun getItemCount(): Int {
        if (exampleList != null) {
            return exampleList.size
        }
        return -1
    }
}