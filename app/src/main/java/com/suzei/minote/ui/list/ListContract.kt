package com.suzei.minote.ui.list


import com.suzei.minote.data.entity.Notes

interface ListContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun showListOfNotes(listOfNotes: MutableList<Notes>)

        fun showListUnavailable()

        fun insertNoteToList(note: Notes, position: Int)

        fun redirectToEditorActivity(itemId: Int)

    }

    interface Presenter {

        fun start()

        fun deleteNote(note: Notes)

        fun showNoteEditor(itemId: Int)

    }

}
