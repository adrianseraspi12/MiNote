package com.suzei.minote.ui.editor.todo

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.local.entity.TodoItem
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.utils.LogMe
import org.threeten.bp.OffsetDateTime

class EditorTodoPresenter : EditorTodoContract.Presenter {

    private var repository: Repository<Todo>
    private var mView: EditorTodoContract.View
    private var sharedPrefs: SharedPreferences
    private var itemId: String? = null
    private var createdDate: OffsetDateTime? = null
    private var isAutoSave: Boolean = false
    private var saveHandler = Handler(Looper.myLooper()!!)

    internal constructor(itemId: String,
                         sharedPreferences: SharedPreferences,
                         repository: Repository<Todo>,
                         view: EditorTodoContract.View) {
        this.sharedPrefs = sharedPreferences
        this.itemId = itemId
        this.repository = repository
        this.mView = view
        this.isAutoSave = sharedPreferences.getBoolean("auto_save", false)

        mView.setPresenter(this)
    }

    internal constructor(sharedPreferences: SharedPreferences,
                         repository: Repository<Todo>,
                         view: EditorTodoContract.View) {
        this.sharedPrefs = sharedPreferences
        this.repository = repository
        this.mView = view
        this.isAutoSave = sharedPreferences.getBoolean("auto_save", false)

        mView.setPresenter(this)
    }

    override fun setup() {
        mView.setSaveBtnVisibility(isAutoSave)

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
            if (isAutoSave) return
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
                    if (isAutoSave) return
                    mView.showToastMessage("Todo Created")
                }

                override fun onFailed() {
                    mView.showToastMessage("Save Failed")
                }

            })
        }

    }

    override fun autoSave(title: String, todoItems: List<TodoItem>, noteColor: String, textColor: String) {
        if (!isAutoSave) return
        saveHandler.removeCallbacksAndMessages(null)
        saveHandler.postDelayed(
                { saveTodo(title, todoItems, noteColor, textColor) },
                1000)
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