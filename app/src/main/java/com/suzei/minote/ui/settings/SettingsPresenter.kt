package com.suzei.minote.ui.settings

import android.content.SharedPreferences

class SettingsPresenter(
        private val view: SettingsContract.View,
        private val sharedPreferences: SharedPreferences): SettingsContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun setup() {
        val isCheck = sharedPreferences.getBoolean("auto_save", false)
        view.setDetails(isCheck)
    }

    override fun setAutoSave(isCheck: Boolean) {
        if (isCheck) {
            view.showToastMessage("Auto save is enabled.")
        }

        val editor = sharedPreferences.edit()
        editor.putBoolean("auto_save", isCheck).apply()
    }

}