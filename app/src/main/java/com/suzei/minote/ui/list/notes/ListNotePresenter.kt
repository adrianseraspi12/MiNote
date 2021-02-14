package com.suzei.minote.ui.list.notes

import com.suzei.minote.data.entity.Notes
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe

class ListNotePresenter internal constructor(
        private val dataSourceImpl: Repository<Notes>,
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
        dataSourceImpl.delete(data)
    }

    override fun checkSizeOfList(size: Int) {
        if (size == 0) {
            mView.showListUnavailable()
        }
    }

    private fun showListOfNotes() {
        dataSourceImpl.getListOfData(object : Repository.ListListener<Notes> {

            override fun onDataAvailable(listOfData: MutableList<Notes>) {
                mView.showListOfNotes(listOfData)
            }

            override fun onDataUnavailable() {
                mView.showListUnavailable()
            }

        })
    }

}
