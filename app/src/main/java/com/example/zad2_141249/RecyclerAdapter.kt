package com.example.zad2_141249

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerAdapter(private val gameList: List<GameItem>, private val listener: OnItemClickListener)
        : RecyclerView.Adapter<RecyclerAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_list_item,
            parent, false, )
        return GameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val currentItem = gameList[position]

        holder.posView.text = currentItem.pozycja
        holder.titleView.text = currentItem.tytul
        holder.yearView.text = currentItem.rok
        if (currentItem.opis.length > 300 && currentItem.opis != null) {
            holder.descView.text = currentItem.opis.substring(0, 300)
                .substringBeforeLast(".") + " (...)"
        }
        else {
            holder.descView.text = currentItem.opis
        }

        if (currentItem.obrazek != null && currentItem.obrazek != "") {
            Picasso.get().load(currentItem.obrazek).into(holder.imageView)
        }

    }

    override fun getItemCount(): Int {
        return gameList.size
    }


    inner class GameViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener{
        val imageView: ImageView = itemView.findViewById(R.id.game_image)
        val posView: TextView = itemView.findViewById(R.id.pozycja_gry)
        val titleView: TextView = itemView.findViewById(R.id.game_title_oryg)
        val yearView: TextView = itemView.findViewById(R.id.rok_gry)
        val descView: TextView = itemView.findViewById(R.id.game_desc)

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

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}