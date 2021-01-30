package com.suzei.minote.ui.editor.todo

import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.utils.ColorWheel

interface EditorTodoContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun showTodoDetails(todo: Todo)

        fun showToastMessage(message: String)

        fun setNoteColor(color: Int)

        fun setTextColor(color: Int)

    }

    interface Presenter {

        fun start()

        fun saveTodo(title: String,
                     todoItems: List<TodoItem>,
                     noteColor: String,
                     textColor: String)

    }

}