package com.suzei.minote.ui.editor

interface TodoContract {

    interface View<T> {
        fun showDetails(data: T)
        fun showToastMessage(message: String)
        fun setSaveBtnVisibility(isAutoSaveEnable: Boolean)
        fun setNoteColor(color: Int)
        fun setTextColor(color: Int)
    }

    interface Presenter {
        fun setup()
    }

}