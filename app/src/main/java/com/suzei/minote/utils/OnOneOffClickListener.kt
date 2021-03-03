package com.suzei.minote.utils

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View

abstract class OnOneOffClickListener : View.OnClickListener {

    companion object {
        const val MIN_CLICK_INTERVAL = 600
        var isViewClicked = false
    }

    private var mLastClickTime: Long = 0

    override fun onClick(p0: View?) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime

        mLastClickTime = currentClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL) {
            return
        }

        if (!isViewClicked) {
            isViewClicked = true
            startTimer()
        } else {
            return
        }
        onSingleClick(p0)
    }

    private fun startTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            isViewClicked = false
        }, 600)
    }

    abstract fun onSingleClick(view: View?)
}