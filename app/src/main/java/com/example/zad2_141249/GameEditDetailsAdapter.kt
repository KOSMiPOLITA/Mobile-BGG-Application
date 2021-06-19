package com.example.zad2_141249

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.details_item.view.*

class GameEditDetailsAdapter(private val myList: List<DetailsItem> ) :
    RecyclerView.Adapter<GameEditDetailsAdapter.GEDViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GEDViewHolder {
        Log.i("pomocy-141249-3", "onCreateViewHolder")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.details_item,
            parent, false)
        return GEDViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GEDViewHolder, position: Int) {
        val currentItem = myList[position]
        Log.i("pomocy-141249-3", "onBindViewHolder")
        holder.popName.text = currentItem.prop_name
        holder.popValue.text = currentItem.prop_val
    }

    override fun getItemCount(): Int {
        Log.i("pomocy-141249-3", "getItemCount")
        return myList.size
    }

    class GEDViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val popName: TextView = itemView.prop_name
        val popValue: TextView = itemView.propValue
    }
}
