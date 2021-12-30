package com.example.view_task2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicappservice.Model.Song
import com.example.musicappservice.R


class SongRecyclerViewAdapter(
    var listProduct: ArrayList<Song>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SongRecyclerViewAdapter.RecyclerViewHolder>() {

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.song_layout, parent, false)

        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        holder.itemView.apply {
            holder.itemView.setOnClickListener {
                listener.onItemClick(position)
            }
            findViewById<ImageView>(R.id.imageSong).setImageResource(listProduct[position].imageSong)
            findViewById<TextView>(R.id.name).text = listProduct[position].name
            findViewById<TextView>(R.id.singer).text = listProduct[position].single
        }
    }


    override fun getItemCount(): Int {
        return listProduct.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {
        }

    }

}
