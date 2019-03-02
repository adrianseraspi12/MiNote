package com.suzei.minote.utils.widgets

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import android.util.AttributeSet

class SquareView : AppCompatButton {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        setMeasuredDimension(width, width)
    }

}
