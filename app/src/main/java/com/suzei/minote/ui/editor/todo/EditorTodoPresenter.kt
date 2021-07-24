package com.suzei.minote.ui.editor.todo

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.local.entity.TodoItem
import com.suzei.minote.data.repository.DataSource
import com.suzei.minote.utils.LogMe
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime

class EditorTodoPresenter : EditorTodoContract.Presenter {

    private var mDataSource: DataSource<Todo>
    private var mView: EditorTodoContract.View
    private var sharedPrefs: SharedPreferences
    private var itemId: String? = null
    private var createdDate: OffsetDateTime? = null
    private var isAutoSave: Boolean = false
    private var saveHandler: Handler? = null
    private val scope = MainScope()

    internal constructor(
        itemId: String,
        sharedPreferences: SharedPreferences,
        dataSource: DataSource<Todo>,
        view: EditorTodoContract.View,
        handler: Handler = Handler(Looper.myLooper()!!)
    ) {
        this.sharedPrefs = sharedPreferences
        this.itemId = itemId
        this.mDataSource = dataSource
        this.mView = view
        this.isAutoSave = sharedPreferences.getBoolean("auto_save", false)
        this.saveHandler = handler

        mView.setPresenter(this)
    }

    internal constructor(
        sharedPreferences: SharedPreferences,
        dataSource: DataSource<Todo>,
        view: EditorTodoContract.View,
        handler: Handler = Handler(Looper.myLooper()!!)
    ) {
        this.sharedPrefs = sharedPreferences
        this.mDataSource = dataSource
        this.mView = view
        this.isAutoSave = sharedPreferences.getBoolean("auto_save", false)
        this.saveHandler = handler

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

    override fun saveTodo(
        title: String, todoItems: List<TodoItem>,
        noteColor: String, textColor: String
    ) {
        if (itemId != null) {
            LogMe.info("Presenter = Updating")
            updateTodo(title, todoItems, noteColor, textColor)
        } else {
            LogMe.info("Presenter =  Save")
            createTodo(title, todoItems, noteColor, textColor)
        }

    }

    override fun autoSave(
        title: String,
        todoItems: List<TodoItem>,
        noteColor: String,
        textColor: String
    ) {
        if (!isAutoSave) return
        saveHandler?.removeCallbacksAndMessages(null)
        saveHandler?.postDelayed(
            { saveTodo(title, todoItems, noteColor, textColor) },
            1000
        )
    }

    private fun createTodo(
        title: String, todoItems: List<TodoItem>,
        noteColor: String, textColor: String
    ) {
        scope.launch {
            val todo = Todo(
                title,
                todoItems,
                textColor,
                noteColor
            )

            val result = mDataSource.save(todo)
            if (result is Result.Error) {
                mView.showToastMessage("Save Failed")
                return@launch
            }

            val saveTodo = (result as? Result.Success)?.data
            if (saveTodo == null) {
                mView.showToastMessage("Save Failed")
                return@launch
            }

            this@EditorTodoPresenter.itemId = saveTodo.id
            this@EditorTodoPresenter.createdDate = saveTodo.createdDate
            if (isAutoSave) return@launch
            mView.showToastMessage("Todo Created")
        }
    }

    private fun updateTodo(
        title: String, todoItems: List<TodoItem>,
        noteColor: String, textColor: String
    ) {
        scope.launch {
            val todo = Todo(
                itemId!!,
                title,
                todoItems,
                textColor,
                noteColor,
                createdDate!!
            )


            mDataSource.update(todo)
            if (isAutoSave) return@launch
            mView.showToastMessage("Todo Updated")
        }
    }

    private fun showTodo() {
        scope.launch {
            val result = mDataSource.getData(itemId!!)
            if (result is Result.Error) return@launch

            val todo = (result as? Result.Success)?.data ?: return@launch
            createdDate = todo.createdDate
            mView.showDetails(todo)
        }
    }

    private fun showNewTodo() {
        mView.setNoteColor(Color.parseColor("#FF6464"))
        mView.setTextColor(Color.parseColor("#000000"))
    }
}