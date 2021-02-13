package com.suzei.minote.ui.editor.note

import android.graphics.Color
import com.suzei.minote.data.DataSource
import com.suzei.minote.data.DataSourceImpl
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.utils.LogMe
import org.threeten.bp.OffsetDateTime

class EditorNotePresenter : EditorNoteContract.Presenter {

    private var dataSourceImpl: DataSource

    private var mView: EditorNoteContract.View

    private var itemId: String? = null

    private lateinit var createdDate: OffsetDateTime

    internal constructor(itemId: String, dataSourceImpl: DataSourceImpl, mView: EditorNoteContract.View) {
        this.dataSourceImpl = dataSourceImpl
        this.mView = mView
        this.itemId = itemId

        mView.setPresenter(this)
    }

    internal constructor(dataSourceImpl: DataSourceImpl,
                         mView: EditorNoteContract.View) {
        this.dataSourceImpl = dataSourceImpl
        this.mView = mView

        mView.setPresenter(this)
    }

    override fun setup() {
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
        dataSourceImpl.saveNote(note, object : DataSource.ActionListener {

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
        dataSourceImpl.updateNote(note)
        mView.showToastMessage("Note updated")
    }

    private fun showNewNote() {
        mView.setNoteColor(Color.parseColor("#FF6464"))
        mView.setTextColor(Color.parseColor("#000000"))
    }

    private fun showNote() {
        dataSourceImpl.getNote(itemId!!, object : DataSource.NoteListener {

            override fun onDataAvailable(note: Notes) {

                note.createdDate?.let {
                    createdDate = it
                }

                mView.showNoteDetails(note)
            }

            override fun onDataUnavailable() {

            }

        })
    }

}
