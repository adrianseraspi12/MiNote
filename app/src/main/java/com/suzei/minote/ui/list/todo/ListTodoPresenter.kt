package com.suzei.minote.ui.list.todo

import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.data.repository.TodoRepository
import com.suzei.minote.ui.list.ListContract

class ListTodoPresenter(
        private val repository: TodoRepository,
        private val mView: ListContract.View<Todo>):
        ListContract.Presenter<Todo> {

    init {
        mView.setPresenter(this)
    }

    override fun start() {
        repository.getListOfData(object: Repository.ListListener<Todo> {

            override fun onDataAvailable(listOfData: MutableList<Todo>) {
                mView.showListOfNotes(listOfData)
            }

            override fun onDataUnavailable() {
                mView.showListUnavailable()
            }

        })
    }

    override fun deleteNote(data: Todo) {

    }

    override fun showEditor(itemId: String) {
        mView.redirectToEditorActivity(itemId)
    }

}