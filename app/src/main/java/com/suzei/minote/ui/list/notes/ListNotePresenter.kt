package com.suzei.minote.ui.list.notes

import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Notes
import com.suzei.minote.data.repository.DataSource
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ListNotePresenter internal constructor(
        private val mDataSource: DataSource<Notes>,
        private val mView: ListContract.View<Notes>) :
        ListContract.Presenter<Notes> {

    private val scope = MainScope()

    init {
        LogMe.info("LOG ListNotePresenter = initialized")
        mView.setPresenter(this)
    }

    override fun setup() {
        showListOfNotes()
    }

    override fun delete(data: Notes) {
        scope.launch {
            mDataSource.delete(data)
        }
    }

    private fun showListOfNotes() {
        scope.launch {
            val result = mDataSource.getListOfData()
            if (result is Result.Error) {
                mView.showListUnavailable()
                return@launch
            }

            val listOfData = (result as Result.Success).data?.toMutableList() ?: mutableListOf()
            if (listOfData.isNotEmpty()) {
                mView.showListOfNotes(listOfData)
            } else {
                mView.showListUnavailable()
            }
        }
    }
}
