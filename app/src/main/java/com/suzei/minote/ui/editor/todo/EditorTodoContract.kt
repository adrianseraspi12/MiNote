package com.suzei.minote.ui.editor.todo

import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.local.entity.TodoItem

interface EditorTodoContract {

    interface View {

        fun setPresenter(presenter: Presenter)

        fun showTodoDetails(todo: Todo)

        fun showToastMessage(message: String)

        fun setNoteColor(color: Int)

        fun setSaveBtnVisibility(isAutoSaveEnable: Boolean)

        fun setTextColor(color: Int)

    }

    interface Presenter {

        fun setup()

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