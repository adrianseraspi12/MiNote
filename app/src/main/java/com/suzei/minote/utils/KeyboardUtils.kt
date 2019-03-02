package com.suzei.minote.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {

    fun showKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}
