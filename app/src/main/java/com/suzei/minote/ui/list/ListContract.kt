package com.suzei.minote.ui.list


interface ListContract {

    interface View<T> {

        fun setPresenter(presenter: Presenter<T>)

        fun showListOfNotes(listOfNotes: MutableList<T>)

        fun showListUnavailable()

        fun insertNoteToList(data: T, position: Int)

        fun redirectToEditorActivity(itemId: String)

    }

    interface Presenter<T> {

        fun start()

        fun delete(data: T)

        fun checkSizeOfList(size: Int)

        fun showEditor(itemId: String)

    }

}

interface ToastCallback {

    fun onUndoClick()
    fun onToastDismiss()

}