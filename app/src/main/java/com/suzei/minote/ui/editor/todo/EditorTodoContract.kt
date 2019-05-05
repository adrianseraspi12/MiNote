package com.suzei.minote.ui.editor.todo

import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.utils.ColorWheel

interface EditorTodoContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun showTodoDetails(todo: Todo)

        fun showAddItemDialog()

        fun showAddTask(todoItem: TodoItem)

        fun showUpdatedTask(position: Int, todoItem: TodoItem)

        fun showToastMessage(message: String)

        fun showColorWheel(title: String, initialColor: Int, colorWheel: ColorWheel)

        fun noteColor(noteColor: Int)

        fun textColor(textColor: Int)

    }

    interface Presenter {

        fun start()

        fun saveTodo(title: String,
                     todoItems: List<TodoItem>,
                     noteColor: String,
                     textColor: String)

        fun addTask(task: String)

        fun updateTask(position: Int, todoItem: TodoItem)

        fun noteColorWheel(initialColor: Int)

        fun textColorWheel(initialColor: Int)

    }

}