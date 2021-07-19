package com.suzei.minote.ui.list

class MockListView<T>: ListContract.View<T> {

    var listOfNotes: MutableList<T>? = null
    var resultPresenter: ListContract.Presenter<T>? = null

    override fun setPresenter(presenter: ListContract.Presenter<T>) {
        this.resultPresenter = presenter
    }

    override fun showListOfNotes(listOfNotes: MutableList<T>) {
        this.listOfNotes = listOfNotes
    }

    override fun showListUnavailable() {
        this.listOfNotes = null
    }

}