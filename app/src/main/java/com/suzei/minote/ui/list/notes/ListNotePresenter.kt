package com.suzei.minote.ui.list.notes

import com.suzei.minote.data.entity.Notes
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe

class ListNotePresenter internal constructor(
        private val notesRepository: Repository<Notes>,
        private val mView: ListContract.View<Notes>) :
        ListContract.Presenter<Notes> {

    init {
        LogMe.info("LOG ListNotePresenter = initialized")
        mView.setPresenter(this)
    }

    override fun setup() {
        showListOfNotes()
    }

    override fun delete(data: Notes) {
        notesRepository.delete(data)
    }

    private fun showListOfNotes() {
        notesRepository.getListOfData(object : Repository.ListListener<Notes> {

            override fun onDataAvailable(listOfData: MutableList<Notes>) {
                mView.showListOfNotes(listOfData)
            }

            override fun onDataUnavailable() {
                mView.showListUnavailable()
            }

        })
    }
}
