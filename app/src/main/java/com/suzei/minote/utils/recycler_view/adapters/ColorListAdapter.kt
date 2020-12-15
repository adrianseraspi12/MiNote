package com.suzei.minote.utils.recycler_view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R
import kotlinx.android.synthetic.main.item_row_pick_color.view.*

class ColorListAdapter(val data: List<Int>) : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_row_pick_color, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = data[position]
        holder.bind(color)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(color: Int) {
            itemView.item_row_cv_pick_color.setCardBackgroundColor(color)
            itemView.item_row_cv_pick_color.strokeColor = color
        }

    }

}