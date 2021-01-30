package com.suzei.minote.ext

import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlin.math.roundToInt

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
    return (this / resources.displayMetrics.density).roundToInt()
}

fun Fragment.showColorWheel(title: String,
                            initialColor: Int,
                            onColorPicked: ((Int) -> Unit)) {
    ColorPickerDialogBuilder.with(context!!)
            .setTitle(title)
            .initialColor(initialColor)
            .density(6)
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
            .setPositiveButton("Choose") { _, i, _ ->
                onColorPicked.invoke(i)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .build()
            .show()
}

fun Int.setAlpha(alpha: Float): Int {
    val alpha = (Color.alpha(this) * alpha).roundToInt()
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return Color.argb(alpha, red, green, blue)
}