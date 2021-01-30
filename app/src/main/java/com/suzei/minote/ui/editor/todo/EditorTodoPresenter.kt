package com.suzei.minote.ui.editor.todo

import android.graphics.Color
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.data.repository.TodoRepository
import com.suzei.minote.utils.LogMe
import org.threeten.bp.OffsetDateTime

class EditorTodoPresenter : EditorTodoContract.Presenter {

    private var repository: TodoRepository

    private var mView: EditorTodoContract.View

    private var itemId: String? = null

    private var createdDate: OffsetDateTime? = null

    internal constructor(itemId: String,
                         repository:
                         TodoRepository,
                         view: EditorTodoContract.View) {

        this.itemId = itemId
        this.repository = repository
        this.mView = view

        mView.setPresenter(this)
    }

    internal constructor(repository: TodoRepository,
                         view: EditorTodoContract.View) {

        this.repository = repository
        this.mView = view

        mView.setPresenter(this)
    }

    override fun start() {

        if (itemId != null) {
            showTodo()
        } else {
            showNewTodo()
        }

    }

    override fun saveTodo(title: String, todoItems: List<TodoItem>,
                          noteColor: String, textColor: String) {

        if (itemId != null) {
            LogMe.info("Presenter = Updating")

            val todo = Todo(
                    itemId!!,
                    title,
                    todoItems,
                    textColor,
                    noteColor,
                    createdDate!!)

            repository.update(todo)
            mView.showToastMessage("Todo Updated")
        } else {
            LogMe.info("Presenter =  Save")

            val todo = Todo(
                    title,
                    todoItems,
                    textColor,
                    noteColor)

            repository.save(todo, object : Repository.ActionListener {

                override fun onSuccess(itemId: String, createdDate: OffsetDateTime) {
                    this@EditorTodoPresenter.itemId = itemId
                    this@EditorTodoPresenter.createdDate = createdDate
                    mView.showToastMessage("Todo Created")
                }

                override fun onFailed() {
                    mView.showToastMessage("Save Failed")
                }

            })
        }

    }

    private fun showTodo() {
        repository.getData(itemId!!, object : Repository.Listener<Todo> {

            override fun onDataAvailable(data: Todo) {
                createdDate = data.createdDate
                mView.showTodoDetails(data)
            }

            override fun onDataUnavailable() {

            }

        })
    }

    private fun showNewTodo() {
        mView.setNoteColor(Color.parseColor("#FF6464"))
        mView.setTextColor(Color.parseColor("#000000"))
    }

}