package com.suzei.minote.utils.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import com.google.android.material.card.MaterialCardView

class CircleView : MaterialCardView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth

        val currentDp = convertToDp(width)

        if (currentDp > 80) {
            val newPx = convertToPixels(80)
            radius = (newPx / 2).toFloat()
            setMeasuredDimension(newPx, newPx)
        } else {
            radius = (width / 2).toFloat()
            setMeasuredDimension(width, width)
        }
    }

    private fun convertToPixels(dp: Int): Int {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                resources.displayMetrics
        ).toInt()
    }

    private fun convertToDp(px: Int): Int {
        val scale = resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
        return px / scale
    }

}
