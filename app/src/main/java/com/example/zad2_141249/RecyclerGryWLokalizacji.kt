package com.example.zad2_141249

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dodatki_item.view.*
import kotlinx.android.synthetic.main.lokalizacje_item.view.*

class RecyclerGryWLokalizacji(private val exampleList: ArrayList<String>?) :
    RecyclerView.Adapter<RecyclerGryWLokalizacji.GraWLokViewHolder>() {

    class GraWLokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nazwa: TextView = itemView.dodatek_tytul_find }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraWLokViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.dodatki_item, parent, false)
        return GraWLokViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GraWLokViewHolder, position: Int) {
        val currentItem = exampleList?.get(position)
        holder.nazwa.text = currentItem
    }

    override fun getItemCount(): Int {
        if (exampleList != null) {
            return exampleList.size
        }
        return -1
    }
}