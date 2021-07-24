package com.suzei.minote.ui.editor.todo

import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.local.entity.TodoItem
import com.suzei.minote.ui.editor.TodoContract

interface EditorTodoContract {
    interface View : TodoContract.View<Todo> {
        fun setPresenter(presenter: Presenter)
    }

    interface Presenter : TodoContract.Presenter {
        fun saveTodo(title: String,
                     todoItems: List<TodoItem>,
                     noteColor: String,
                     textColor: String)

        fun autoSave(title: String,
                     todoItems: List<TodoItem>,
                     noteColor: String,
                     textColor: String)
    }

}