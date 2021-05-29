package com.suzei.minote.ui.editor.note

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Notes
import com.suzei.minote.data.repository.DataSource
import com.suzei.minote.utils.LogMe
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime

class EditorNotePresenter : EditorNoteContract.Presenter {

    private var mDataSource: DataSource<Notes>
    private var mView: EditorNoteContract.View
    private var sharedPrefs: SharedPreferences
    private var itemId: String? = null
    private var isAutoSave: Boolean = false
    private var saveHandler = Handler(Looper.myLooper()!!)
    private val scope = MainScope()

    private lateinit var createdDate: OffsetDateTime

    internal constructor(itemId: String,
                         sharedPreferences: SharedPreferences,
                         dataSource: DataSource<Notes>,
                         mView: EditorNoteContract.View) {
        this.sharedPrefs = sharedPreferences
        this.mDataSource = dataSource
        this.mView = mView
        this.itemId = itemId
        this.isAutoSave = sharedPreferences.getBoolean("auto_save", false)

        mView.setPresenter(this)
    }

    internal constructor(dataSource: DataSource<Notes>,
                         sharedPreferences: SharedPreferences,
                         mView: EditorNoteContract.View) {
        this.sharedPrefs = sharedPreferences
        this.mDataSource = dataSource
        this.mView = mView
        this.isAutoSave = sharedPreferences.getBoolean("auto_save", false)

        mView.setPresenter(this)
    }

    override fun setup() {
        mView.setSaveBtnVisibility(isAutoSave)

        if (itemId != null) {
            showNote()
        } else {
            showNewNote()
        }
    }

    override fun saveNote(title: String,
                          message: String,
                          noteColor: String,
                          textColor: String,
                          password: String?) {
        LogMe.info("Item Id = $itemId")

        if (itemId != null) {
            val note = Notes(
                    itemId!!,
                    title,
                    password,
                    message,
                    textColor,
                    noteColor,
                    createdDate)

            updateNote(note)
        } else {
            val note = Notes(
                    title,
                    password,
                    message,
                    textColor,
                    noteColor)

            createNote(note)
        }
    }

    override fun autoSave(title: String,
                          message: String,
                          noteColor: String,
                          textColor: String,
                          password: String?) {
        if (!isAutoSave) return
        saveHandler.removeCallbacksAndMessages(null)
        saveHandler.postDelayed(
                { saveNote(title, message, noteColor, textColor, password) },
                1000)
    }

    private fun createNote(note: Notes) {
        scope.launch {
            val result = mDataSource.save(note)
            if (result is Result.Error) {
                mView.showToastMessage("Save Failed")
                return@launch
            }
            val saveNote = (result as Result.Success).data
            if (saveNote == null) {
                mView.showToastMessage("Save Failed")
                return@launch
            }

            this@EditorNotePresenter.itemId = saveNote.id
            this@EditorNotePresenter.createdDate = saveNote.createdDate!!
            if (isAutoSave) return@launch
            mView.showToastMessage("Note created")
        }
    }

    private fun updateNote(note: Notes) {
        scope.launch {
            mDataSource.update(note)
            if (isAutoSave) return@launch
            mView.showToastMessage("Note saved")
        }
    }

    private fun showNewNote() {
        mView.setNoteColor(Color.parseColor("#FF6464"))
        mView.setTextColor(Color.parseColor("#000000"))
    }

    private fun showNote() {
        scope.launch {
            val result = mDataSource.getData(itemId!!)
            if (result is Result.Error) {
                mView.showToastMessage("Unable to find the note. Please try again.")
                return@launch
            }
            val note = (result as Result.Success).data
            if (note == null) {
                mView.showToastMessage("Unable to find the note. Please try again.")
                return@launch
            }

            note.createdDate?.let {
                createdDate = it
            }
            mView.showDetails(note)
        }
    }
}
