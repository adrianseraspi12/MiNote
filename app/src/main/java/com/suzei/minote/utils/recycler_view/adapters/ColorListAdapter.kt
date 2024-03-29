package com.suzei.minote.utils.recycler_view.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.databinding.ItemRowPickColorBinding

class ColorListAdapter(
        private val data: MutableList<Pair<Int, Boolean>>,
        private val callback: ColorListAdapterCallback
) : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {

    private var currentSelectedColorPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRowPickColorBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val colorData = data[position]
        holder.setSelected(colorData.second)

        if (position == 5) {
            holder.bindCustomColor()
        } else {
            holder.bind(colorData.first)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setSelectedColor(color: Int) {
        val oldData = data[currentSelectedColorPos]
        val selectedColorPosition = getPosition(color)

        //  Set unselected Color
        data[currentSelectedColorPos] = Pair(oldData.first, false)
        notifyItemChanged(currentSelectedColorPos)

        currentSelectedColorPos = selectedColorPosition
        if (selectedColorPosition == 5) {
            //  Set selected color
            val pair = data[selectedColorPosition]
            data[selectedColorPosition] = Pair(pair.first, true)
            notifyItemChanged(selectedColorPosition)
            return
        }

        //  Set selected color
        data[selectedColorPosition] = Pair(color, true)
        notifyItemChanged(selectedColorPosition)
    }

    private fun getPosition(color: Int): Int {
        val colors = data.map { it.first }
        return if (!colors.contains(color)) {
            5
        } else {
            colors.indexOf(color)
        }
    }

    inner class ViewHolder(val binding: ItemRowPickColorBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.itemRowCvPickColor.setOnClickListener {
                val colorData = data[adapterPosition]
                val oldData = data[currentSelectedColorPos]

                if (adapterPosition == data.lastIndex) {
                    //  Show Color Picker Dialog
                    callback.onShowColorWheel(oldData.first)
                    return@setOnClickListener
                }
                setSelectedColor(colorData.first)
                callback.onChangedColor(colorData.first)
            }
        }

        fun bind(color: Int) {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = binding.itemRowCvPickColor.radius
            gradientDrawable.setColor(color)
            binding.itemRowCvPickColor.background = gradientDrawable
        }

        fun bindCustomColor() {
            //  Set a gradient custom color
            val arr = intArrayOf(
                    Color.parseColor("#FF6464"),
                    Color.parseColor("#FFA500"),
                    Color.parseColor("#FDEC61"),
                    Color.parseColor("#96F07B"),
                    Color.parseColor("#76A0FF"))

            val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, arr)
            gradientDrawable.cornerRadius = binding.itemRowCvPickColor.radius
            binding.itemRowCvPickColor.background = gradientDrawable
        }

        fun setSelected(isSelected: Boolean) {
            if (isSelected) {
                binding.itemRowCvPickColor.strokeColor = Color.parseColor("#FAB73B")
            } else {
                binding.itemRowCvPickColor.strokeColor = Color.TRANSPARENT
            }
        }

    }

}

interface ColorListAdapterCallback {

    fun onChangedColor(color: Int)
    fun onShowColorWheel(color: Int)

}