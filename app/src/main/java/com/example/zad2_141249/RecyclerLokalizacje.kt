package com.example.zad2_141249

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.lokalizacje_item.view.*

class RecyclerLokalizacje(private val exampleList: ArrayList<String>?, private val listener: Lokalizacje) :
    RecyclerView.Adapter<RecyclerLokalizacje.LokalizacjaItemViewHolder>() {

    inner class LokalizacjaItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener{
        val nazwa: TextView = itemView.tytulLokalizacji
        val buttonDel: ImageButton = itemView.usunLokalizacja

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LokalizacjaItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lokalizacje_item,
            parent, false)
        return LokalizacjaItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LokalizacjaItemViewHolder, position: Int) {
        val currentItem = exampleList?.get(position)
        holder.nazwa.text = currentItem
        holder.buttonDel.setOnClickListener {
            exampleList?.removeAt(position)
            Log.i("pomocy-141249-us", "LOK $currentItem")
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        if (exampleList != null) {
            return exampleList.size
        }
        return -1
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}