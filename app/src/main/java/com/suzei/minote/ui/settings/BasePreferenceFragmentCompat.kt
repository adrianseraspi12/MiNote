package com.suzei.minote.ui.settings

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R

abstract class BasePreferenceFragmentCompat : PreferenceFragmentCompat() {
    override fun onCreateAdapter(preferenceScreen: PreferenceScreen): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {
            @SuppressLint("RestrictedApi")
            override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)
                val preference = getItem(position)
                if (preference is PreferenceCategory)
                    setZeroPaddingToLayoutChildren(holder.itemView)
                else {
                    val iconFrame = holder.itemView.findViewById<View>(R.id.icon_frame)
                    if (iconFrame != null) {
                        iconFrame.visibility = if (preference.icon == null) View.GONE else View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setZeroPaddingToLayoutChildren(view: View) {
        if (view !is ViewGroup)
            return
        val childCount = view.childCount
        for (i in 0 until childCount) {
            setZeroPaddingToLayoutChildren(view.getChildAt(i))
            view.setPaddingRelative(0, view.paddingTop, view.paddingEnd, view.paddingBottom)
        }
    }
}
