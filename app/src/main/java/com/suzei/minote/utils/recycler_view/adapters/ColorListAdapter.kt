package com.suzei.minote.utils.recycler_view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R
import kotlinx.android.synthetic.main.item_row_pick_color.view.*

class ColorListAdapter(val data: MutableList<Pair<Int, Boolean>>,
                       val onChangeColorListener: ((Int) -> Unit)
) : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {

    private var currentSelectedColorPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_row_pick_color, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val colorData = data[position]
        holder.bind(colorData.first, colorData.second)
        holder.itemView.item_row_cv_pick_color.setOnClickListener {
            val oldData = data[currentSelectedColorPos]
            data[currentSelectedColorPos] = Pair(oldData.first, false)
            notifyItemChanged(currentSelectedColorPos)

            data[position] = Pair(colorData.first, true)
            currentSelectedColorPos = position
            notifyItemChanged(position)

            onChangeColorListener.invoke(colorData.first)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSelectedColor(color: Int) {
        data.forEachIndexed { index, pair ->
            if (pair.first == color) {
                currentSelectedColorPos = index
                data[index] = Pair(color, true)
                notifyItemChanged(index)
            } else if (index == data.size) {
                currentSelectedColorPos = index
                data[index] = Pair(pair.first, true)
                notifyItemChanged(index)
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(color: Int, isSelected: Boolean) {
            itemView.item_row_cv_pick_color.setCardBackgroundColor(color)

            if (isSelected) {
                itemView.item_row_cv_pick_color.strokeColor = Color.parseColor("#FAB73B")
            } else {
                itemView.item_row_cv_pick_color.strokeColor = color
            }
        }

    }

}