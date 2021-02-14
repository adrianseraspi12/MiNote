package com.suzei.minote.ui.list


interface ListContract {

    interface View<T> {

        fun setPresenter(presenter: Presenter<T>)

        fun showListOfNotes(listOfNotes: MutableList<T>)

        fun showListUnavailable()

    }

    interface Presenter<T> {

        fun start()

        fun delete(data: T)

    }

}

interface ToastCallback {

    fun onUndoClick()
    fun onToastDismiss()

}