package com.suzei.minote.ui.editor.note

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import com.suzei.minote.data.local.entity.Notes
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.utils.LogMe
import org.threeten.bp.OffsetDateTime

class EditorNotePresenter : EditorNoteContract.Presenter {

    private var notesRepository: Repository<Notes>
    private var mView: EditorNoteContract.View
    private var sharedPrefs: SharedPreferences
    private var itemId: String? = null
    private var isAutoSave: Boolean = false
    private var saveHandler = Handler(Looper.myLooper()!!)

    private lateinit var createdDate: OffsetDateTime

    internal constructor(itemId: String,
                         sharedPreferences: SharedPreferences,
                         notesRepository: Repository<Notes>,
                         mView: EditorNoteContract.View) {
        this.sharedPrefs = sharedPreferences
        this.notesRepository = notesRepository
        this.mView = mView
        this.itemId = itemId
        this.isAutoSave = sharedPreferences.getBoolean("auto_save", false)

        mView.setPresenter(this)
    }

    internal constructor(notesRepository: Repository<Notes>,
                         sharedPreferences: SharedPreferences,
                         mView: EditorNoteContract.View) {
        this.sharedPrefs = sharedPreferences
        this.notesRepository = notesRepository
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
        notesRepository.save(note, object : Repository.ActionListener {

            override fun onSuccess(itemId: String, createdDate: OffsetDateTime) {
                this@EditorNotePresenter.itemId = itemId
                this@EditorNotePresenter.createdDate = createdDate
                if (isAutoSave) return
                mView.showToastMessage("Note created")
            }

            override fun onFailed() {
                mView.showToastMessage("Save Failed")
            }

        })

    }

    private fun updateNote(note: Notes) {
        notesRepository.update(note)
        if (isAutoSave) return
        mView.showToastMessage("Note saved")
    }

    private fun showNewNote() {
        mView.setNoteColor(Color.parseColor("#FF6464"))
        mView.setTextColor(Color.parseColor("#000000"))
    }

    private fun showNote() {
        notesRepository.getData(itemId!!, object : Repository.Listener<Notes> {
            override fun onDataAvailable(data: Notes) {

                data.createdDate?.let {
                    createdDate = it
                }

                mView.showNoteDetails(data)
            }

            override fun onDataUnavailable() {}
        })
    }
}
