package com.suzei.minote.ui.editor

import com.suzei.minote.ui.editor.todo.EditorTodoContract

interface TodoContract {

    interface View<T> {
        fun setPresenter(presenter: EditorTodoContract.Presenter)
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