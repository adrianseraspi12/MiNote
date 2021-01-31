package com.suzei.minote.ui.list.notes

import com.suzei.minote.data.DataSource
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe

class ListNotePresenter internal constructor(
        private val dataSourceImpl:
        DataSource,
        private val mView: ListContract.View<Notes>) :
        ListContract.Presenter<Notes> {

    init {
        LogMe.info("LOG ListNotePresenter = initialized")
        mView.setPresenter(this)
    }

    override fun start() {
        showListOfNotes()
    }

    override fun delete(data: Notes) {
        dataSourceImpl.deleteNote(data)
    }

    override fun checkSizeOfList(size: Int) {
        if (size == 0) {
            mView.showListUnavailable()
        }
    }

    private fun showListOfNotes() {
        dataSourceImpl.getListOfNotes(object : DataSource.ListNoteListener {

            override fun onDataAvailable(listOfNote: MutableList<Notes>) {
                mView.showListOfNotes(listOfNote)
            }

            override fun onDataUnavailable() {
                mView.showListUnavailable()
            }

        })
    }

}
