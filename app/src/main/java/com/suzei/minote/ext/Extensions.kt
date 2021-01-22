package com.suzei.minote.ext

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.EditText

fun EditText.moveFocus() {
    val pos = text.length
    setSelection(pos)
}

fun Int.convertToPx(resources: Resources): Int {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
    ).toInt()
}

fun Int.convertToDp(resources: Resources): Int {
    val scale = resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    return this / scale
}