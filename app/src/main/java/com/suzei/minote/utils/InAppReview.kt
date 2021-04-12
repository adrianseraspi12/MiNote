package com.suzei.minote.utils

import android.app.Activity
import com.google.android.play.core.review.testing.FakeReviewManager

class InAppReview {

    companion object {

        fun run(activity: Activity) {
            val manager = FakeReviewManager(activity)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { pRequest ->
                if (pRequest.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = request.result
                    manager.launchReviewFlow(activity, reviewInfo)
                }
            }
        }

    }
}