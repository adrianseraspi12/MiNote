package com.suzei.minote.ui.editor.todo

import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.data.repository.TodoRepository
import com.suzei.minote.utils.LogMe

class EditorTodoPresenter(
        private var repository: TodoRepository,
        private var mView: EditorTodoContract.View):
        EditorTodoContract.Presenter {

    init {
        mView.setPresenter(this)
    }

    override fun start() {

    }

    override fun saveTodo(title: String, todoItems: List<TodoItem>) {
        //  Add note color, text color
        LogMe.info("Presenter =  Save")
        val todo = Todo(title, todoItems, "#FFF", "#000")
        repository.save(todo)
    }

    override fun addTask(task: String) {
        mView.showAddTodoItem(
                TodoItem(task, false)
        )
    }

}