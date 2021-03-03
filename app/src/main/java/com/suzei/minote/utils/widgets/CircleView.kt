package com.suzei.minote.utils.widgets

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import com.suzei.minote.ext.convertToDp
import com.suzei.minote.ext.convertToPx

class CircleView : MaterialCardView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height = measuredHeight
        val currentDp = height.convertToDp(resources)

        if (currentDp > 80) {
            val newPx = 80.convertToPx(resources)
            radius = (newPx / 2).toFloat()
            setMeasuredDimension(newPx, newPx)
        } else {
            radius = (height / 2).toFloat()
            setMeasuredDimension(height, height)
        }
    }
}
