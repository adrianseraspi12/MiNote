package com.suzei.minote.ui.editor.note

import com.suzei.minote.data.local.entity.Notes
import com.suzei.minote.ui.editor.TodoContract

interface EditorNoteContract {

    interface View : TodoContract.View<Notes> {
        fun setPresenter(presenter: Presenter)
    }

    interface Presenter : TodoContract.Presenter {
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
