package com.suzei.minote.ui.editor.note

import android.content.SharedPreferences
import android.graphics.Color
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.utils.LogMe
import org.threeten.bp.OffsetDateTime

class EditorNotePresenter : EditorNoteContract.Presenter {

    private var notesRepository: Repository<Notes>
    private var mView: EditorNoteContract.View
    private var sharedPrefs: SharedPreferences
    private var itemId: String? = null


    private lateinit var createdDate: OffsetDateTime

    internal constructor(itemId: String,
                         sharedPreferences: SharedPreferences,
                         notesRepository: Repository<Notes>,
                         mView: EditorNoteContract.View) {
        this.sharedPrefs = sharedPreferences
        this.notesRepository = notesRepository
        this.mView = mView
        this.itemId = itemId

        mView.setPresenter(this)
    }

    internal constructor(notesRepository: Repository<Notes>,
                         sharedPreferences: SharedPreferences,
                         mView: EditorNoteContract.View) {
        this.sharedPrefs = sharedPreferences
        this.notesRepository = notesRepository
        this.mView = mView

        mView.setPresenter(this)
    }

    override fun setup() {
        val isAutoSaveEnable = sharedPrefs.getBoolean("auto_save", false)
        mView.setSaveBtnVisibility(isAutoSaveEnable)

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

    private fun createNote(note: Notes) {
        notesRepository.save(note, object : Repository.ActionListener {

            override fun onSuccess(itemId: String, createdDate: OffsetDateTime) {
                this@EditorNotePresenter.itemId = itemId
                this@EditorNotePresenter.createdDate = createdDate
                mView.showToastMessage("Note created")
            }

            override fun onFailed() {
                mView.showToastMessage("Save Failed")
            }

        })

    }

    private fun updateNote(note: Notes) {
        notesRepository.update(note)
        mView.showToastMessage("Note updated")
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

            override fun onDataUnavailable() {

            }

        })
    }

}
