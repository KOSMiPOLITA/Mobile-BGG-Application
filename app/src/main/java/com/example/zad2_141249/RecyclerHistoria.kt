package com.example.zad2_141249

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.history_list_item.view.*

class RecyclerHistoria(private val exampleList: ArrayList<HistoriaItem>?) :
    RecyclerView.Adapter<RecyclerHistoria.HistoryItemViewHolder>() {

    class HistoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dataH: TextView = itemView.historiaData
        val rankH: TextView = itemView.historiaRanking
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_list_item,
            parent, false)
        return HistoryItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        val currentItem = exampleList?.get(position)

        if (currentItem != null) {
            holder.dataH.text = currentItem.data
        }
        if (currentItem != null) {
            holder.rankH.text = currentItem.ranking
        }
    }

    override fun getItemCount(): Int {
        if (exampleList != null) {
            return exampleList.size
        }
        return -1
    }
}