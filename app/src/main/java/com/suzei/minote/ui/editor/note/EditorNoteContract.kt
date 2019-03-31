package com.suzei.minote.ui.editor.note

import com.suzei.minote.data.entity.Notes
import com.suzei.minote.utils.ColorWheel

interface EditorNoteContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun showNoteDetails(note: Notes)

        fun noteColor(noteColor: Int)

        fun textColor(textColor: Int)

        fun showToastMessage(message: String)

        fun showColorWheel(title: String, initialColor: Int, colorWheel: ColorWheel)

        fun showPasswordDialog()
    }

    interface Presenter {

        fun start()

        fun saveNote(title: String,
                     message: String,
                     noteColor: String,
                     textColor: String,
                     password: String?)

        fun passwordDialog()

        fun noteColorWheel(initialColor: Int)

        fun textColorWheel(initialColor: Int)

    }

}
