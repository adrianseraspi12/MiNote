package com.suzei.minote.ext

import android.widget.EditText

fun EditText.moveFocus() {
    val pos = text.length
    setSelection(pos)
}