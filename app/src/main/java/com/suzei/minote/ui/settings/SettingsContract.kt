package com.suzei.minote.ui.settings

import android.content.Intent

import com.suzei.minote.utils.ColorWheel

interface SettingsContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun showColorWheel(title: String, initialColor: String, colorWheel: ColorWheel)

        fun startIntentActivity(intent: Intent)

    }

    interface Presenter {

        fun noteColorWheel()

        fun textColorWheel()

        fun redirectToEmail()

        fun redirectToPlaystore()

    }

}
