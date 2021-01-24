package com.suzei.minote.ui.editor.note

import com.suzei.minote.data.entity.Notes
import com.suzei.minote.utils.ColorWheel

interface EditorNoteContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun showNoteDetails(note: Notes)

        fun setNoteColor(noteColor: Int)

        fun setTextColor(textColor: Int)

        fun showToastMessage(message: String)
    }

    interface Presenter {

        fun start()

        fun saveNote(title: String,
                     message: String,
                     noteColor: String,
                     textColor: String,
                     password: String?)

    }

}
