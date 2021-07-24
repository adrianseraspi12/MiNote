package com.suzei.minote.ui.list.todo

import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.repository.DataSource
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ListTodoPresenter(
        private val todoDataSource: DataSource<Todo>,
        private val mView: ListContract.View<Todo>) :
        ListContract.Presenter<Todo> {

    private val scope = MainScope()

    init {
        LogMe.info("LOG ListTodoPresenter = initialized")
        mView.setPresenter(this)
    }

    override fun setup() {
        showListOfTodo()
    }

    override fun delete(data: Todo) {
        scope.launch {
            todoDataSource.delete(data)
        }
    }

    private fun showListOfTodo() {
        scope.launch {
            val result = todoDataSource.getListOfData()
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