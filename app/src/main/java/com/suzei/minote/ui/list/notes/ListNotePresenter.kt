package com.suzei.minote.ui.list.notes

import com.suzei.minote.data.DataSource
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.ui.list.ListContract

class ListNotePresenter internal constructor(
        private val dataSourceImpl:
        DataSource,
        private val mView: ListContract.View<Notes>):
        ListContract.Presenter<Notes> {

    init {
        mView.setPresenter(this)
    }

    override fun start() {
        showListOfNotes()
    }

    override fun deleteNote(note: Notes) {
        dataSourceImpl.deleteNote(note)
    }

    override fun showNoteEditor(itemId: Int) {
        mView.redirectToEditorActivity(itemId)
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
