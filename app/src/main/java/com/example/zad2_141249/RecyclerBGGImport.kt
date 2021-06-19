package com.example.zad2_141249

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dodatki_item.view.*
import kotlinx.android.synthetic.main.ekran_bgg_item.view.*


class RecyclerBGGImport(private val exampleList: List<GraImportowana>?, private val listener: RecyclerAdapter.OnItemClickListener) :
    RecyclerView.Adapter<RecyclerBGGImport.ImportowanaViewHolder>()  {

    inner class ImportowanaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener{
        val nazwa: TextView = itemView.bggTytulGry
        val idzBGG: TextView = itemView.bggIDzBGG
        val imageBGG: ImageView = itemView.bggImage

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImportowanaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ekran_bgg_item, parent, false)
        return ImportowanaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImportowanaViewHolder, position: Int) {
        val currentItem = exampleList?.get(position)
        holder.nazwa.text = currentItem?.tytul
        holder.idzBGG.text = currentItem?.idBBG.toString()
        if (currentItem!!.zdjecie != null && currentItem!!.zdjecie != "") {
            Picasso.get().load(currentItem.zdjecie).into(holder.imageBGG)
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