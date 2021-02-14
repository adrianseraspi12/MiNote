package com.suzei.minote.ui.list.todo

import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe

class ListTodoPresenter(
        private val repository: Repository<Todo>,
        private val mView: ListContract.View<Todo>) :
        ListContract.Presenter<Todo> {

    init {
        LogMe.info("LOG ListTodoPresenter = initialized")
        mView.setPresenter(this)
    }

    override fun start() {
        repository.getListOfData(object : Repository.ListListener<Todo> {

            override fun onDataAvailable(listOfData: MutableList<Todo>) {
                mView.showListOfNotes(listOfData)
            }

            override fun onDataUnavailable() {
                mView.showListUnavailable()
            }

        })
    }

    override fun delete(data: Todo) {
        repository.delete(data)
    }
}