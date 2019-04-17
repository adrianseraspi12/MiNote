package com.suzei.minote.ui.list


interface ListContract {

    interface View<T> {

        fun setPresenter(presenter: Presenter<T>)

        fun showListOfNotes(listOfNotes: MutableList<T>)

        fun showListUnavailable()

        fun insertNoteToList(data: T, position: Int)

        fun redirectToEditorActivity(itemId: Int)

    }

    interface Presenter<T> {

        fun start()

        fun deleteNote(data: T)

        fun showNoteEditor(itemId: Int)

    }

}
