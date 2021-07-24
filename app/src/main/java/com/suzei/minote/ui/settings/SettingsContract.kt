package com.suzei.minote.ui.settings

interface SettingsContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun setDetails(isCheck: Boolean)

        fun showToastMessage(message: String)

    }

    interface Presenter {

        fun setup()

        fun setAutoSave(isCheck: Boolean)

    }

}