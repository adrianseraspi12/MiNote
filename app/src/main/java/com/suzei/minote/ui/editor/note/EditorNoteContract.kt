package com.suzei.minote.ui.editor.note

import com.suzei.minote.data.entity.Notes

interface EditorNoteContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun showNoteDetails(note: Notes)

        fun setNoteColor(noteColor: Int)

        fun setTextColor(textColor: Int)

        fun setSaveBtnVisibility(isAutoSaveEnable: Boolean)

        fun showToastMessage(message: String)
    }

    interface Presenter {

        fun setup()

        fun saveNote(title: String,
                     message: String,
                     noteColor: String,
                     textColor: String,
                     password: String?)

        fun autoSave(title: String,
                     message: String,
                     noteColor: String,
                     textColor: String,
                     password: String?)
    }

}
