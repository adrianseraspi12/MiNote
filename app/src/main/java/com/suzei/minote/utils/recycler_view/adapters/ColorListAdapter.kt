package com.suzei.minote.utils.recycler_view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R
import com.suzei.minote.utils.LogMe
import kotlinx.android.synthetic.main.item_row_pick_color.view.*

class ColorListAdapter(
        private val data: MutableList<Pair<Int, Boolean>>,
        private val onChangeColorListener: ((Int) -> Unit),
        private val onShowColorWheel: ((Int) -> Unit)
) : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {

    private var currentSelectedColorPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_row_pick_color, parent, false)
        LogMe.info("DATA = $data")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val colorData = data[position]
        holder.bind(colorData.first, colorData.second)
        holder.itemView.item_row_cv_pick_color.setOnClickListener {
            val oldData = data[currentSelectedColorPos]

            if (position == data.lastIndex) {
                //  Show Color Picker Dialog
                onShowColorWheel.invoke(oldData.first)
                return@setOnClickListener
            }
            setSelectedColor(colorData.first)
            onChangeColorListener.invoke(colorData.first)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSelectedColor(color: Int) {
        val oldData = data[currentSelectedColorPos]
        var currentIndex = 0

        for (pair in data) {

            if (pair.first == color) {
                //  Set unselected Color
                data[currentSelectedColorPos] = Pair(oldData.first, false)
                notifyItemChanged(currentSelectedColorPos)

                //  Set selected color
                currentSelectedColorPos = currentIndex
                data[currentIndex] = Pair(color, true)
                notifyItemChanged(currentIndex)
                break

            } else if (currentIndex == 5) {
                //  Set unselected Color
                data[currentSelectedColorPos] = Pair(oldData.first, false)
                notifyItemChanged(currentSelectedColorPos)

                //  Set selected color
                currentSelectedColorPos = currentIndex
                data[currentIndex] = Pair(pair.first, true)
                notifyItemChanged(currentIndex)
            }

            currentIndex++
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(color: Int, isSelected: Boolean) {
            itemView.item_row_cv_pick_color.setCardBackgroundColor(color)

            LogMe.info("COLOR = $color ||| isSelected = $isSelected ||| Position = $adapterPosition")

            if (isSelected) {
                itemView.item_row_cv_pick_color.strokeColor = Color.parseColor("#FAB73B")
            } else {
                itemView.item_row_cv_pick_color.strokeColor = color
            }
        }

    }

}