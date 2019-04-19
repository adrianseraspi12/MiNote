package com.suzei.minote.ui.editor.todo

import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem

interface EditorTodoContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun showTodoDetails(todo: Todo)

        fun showAddItemDialog()

        fun showAddTask(todoItem: TodoItem)

        fun showUpdatedTask(position: Int, todoItem: TodoItem)

        fun showToastMessage(message: String)
        // note color
        // text color

    }

    interface Presenter {

        fun start()

        fun saveTodo(title: String,
                     todoItems: List<TodoItem>)

        fun addTask(task: String)

        fun updateTask(position: Int, todoItem: TodoItem)

    }

}