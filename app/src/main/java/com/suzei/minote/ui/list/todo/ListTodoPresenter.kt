package com.suzei.minote.ui.list.todo

import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.data.repository.TodoRepository
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe

class ListTodoPresenter(
        private val repository: TodoRepository,
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

    override fun checkSizeOfList(size: Int) {
        if (size == 0) {
            mView.showListUnavailable()
        }
    }
}