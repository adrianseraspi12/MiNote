package com.suzei.minote.utils.recycler_view.adapters

import android.graphics.Color
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapter
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapterCallback

object ColorListAdapterBuilder {

    fun noteList(callback: ColorListAdapterCallback): ColorListAdapter {
        val noteColors = mutableListOf(
                Pair(Color.parseColor("#FF6464"), true),
                Pair(Color.parseColor("#FDEC61"), false),
                Pair(Color.parseColor("#76A0FF"), false),
                Pair(Color.parseColor("#96F07B"), false),
                Pair(Color.parseColor("#FF8EEE"), false),
                Pair(Color.parseColor("#F5DEB3"), false),
        )
        return ColorListAdapter(noteColors, callback)
    }

    fun textList(callback: ColorListAdapterCallback): ColorListAdapter {
        val textColors = mutableListOf(
                Pair(Color.parseColor("#000000"), true),
                Pair(Color.parseColor("#FFFFFF"), false),
                Pair(Color.parseColor("#FF6464"), false),
                Pair(Color.parseColor("#FDEC61"), false),
                Pair(Color.parseColor("#76A0FF"), false),
                Pair(Color.parseColor("#F5DEB3"), false),
        )
        return ColorListAdapter(textColors, callback)
    }

}