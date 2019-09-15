package com.suzei.minote.ui.editor.todo

import android.content.SharedPreferences
import android.graphics.Color
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.data.repository.TodoRepository
import com.suzei.minote.utils.ColorWheel
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
        }
        else {
            LogMe.info("Presenter =  Save")

            val todo = Todo(
                    title,
                    todoItems,
                    textColor,
                    noteColor)

            repository.save(todo, object: Repository.ActionListener {

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

    override fun addTask(task: String) {
        if (task.isNotEmpty()) {
            mView.showAddTask(
                    TodoItem(task, false)
            )
        }
    }

    override fun updateTask(position: Int, todoItem: TodoItem) {
        mView.showUpdatedTask(position, todoItem)
    }

    override fun noteColorWheel(initialColor: Int) {
        mView.showColorWheel(
                "Choose note color",
                initialColor,
                object: ColorWheel {

                    override fun onPositiveClick(color: Int) {
                        mView.noteColor(color)
                    }

                })
    }

    override fun textColorWheel(initialColor: Int) {
        mView.showColorWheel(
                "Choose text color",
                initialColor,
                object: ColorWheel {

                    override fun onPositiveClick(color: Int) {
                        mView.textColor(color)
                    }

                })
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
        mView.noteColor(Color.parseColor(noteColor))
        mView.textColor(Color.parseColor(textColor))
    }

}