package com.suzei.minote.ui.editor.todo

import android.content.SharedPreferences
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.data.repository.TodoRepository
import com.suzei.minote.utils.LogMe
import org.threeten.bp.OffsetDateTime

class EditorTodoPresenter: EditorTodoContract.Presenter {

    private var repository: TodoRepository

    private var mView: EditorTodoContract.View

    private lateinit var mPrefs: SharedPreferences

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

    internal constructor(prefs: SharedPreferences,
                         repository: TodoRepository,
                         view: EditorTodoContract.View) {

        this.mPrefs = prefs
        this.repository = repository
        this.mView = view

        mView.setPresenter(this)
    }

    override fun start() {

        if (itemId != null) {
            showTodo()
        }
        else {
            showNewTodo()
        }

    }

    override fun saveTodo(title: String, todoItems: List<TodoItem>) {
        //  Add note color, text color

        if (itemId != null) {
            LogMe.info("Presenter = Updating")

            val todo = Todo(
                    itemId!!,
                    title,
                    todoItems,
                    "#FFF",
                    "#000",
                    createdDate!!)

            repository.update(todo)
            mView.showToastMessage("Todo Updated")
        }
        else {
            LogMe.info("Presenter =  Save")

            val todo = Todo(
                    title,
                    todoItems,
                    "#FFF",
                    "#000")

            repository.save(todo)
            mView.showToastMessage("Todo Created")
        }

    }

    override fun addTask(task: String) {
        mView.showAddTask(
                TodoItem(task, false)
        )
    }

    override fun updateTask(position: Int, task: TodoItem) {
        mView.showUpdatedTask(position, task)
    }

    private fun showTodo() {
        repository.getData(itemId!!, object: Repository.Listener<Todo> {

            override fun onDataAvailable(data: Todo) {
                createdDate = data.createdDate
                mView.showTodoDetails(data)
            }

            override fun onDataUnavailable() {

            }

        })
    }

    private fun showNewTodo() {
        val noteColor = mPrefs.getString("default_note_color", "#ef5350")
        val textColor = mPrefs.getString("default_text_color", "#000000")
    }

}