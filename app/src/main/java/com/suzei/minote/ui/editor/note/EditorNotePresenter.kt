package com.suzei.minote.ui.editor.note

import android.content.SharedPreferences
import android.graphics.Color

import com.suzei.minote.data.DataSource
import com.suzei.minote.data.DataSourceImpl
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.utils.ColorWheel
import com.suzei.minote.utils.LogMe
import org.threeten.bp.OffsetDateTime

class EditorNotePresenter : EditorNoteContract.Presenter {

    private var dataSourceImpl: DataSource

    private var mView: EditorNoteContract.View

    private lateinit var prefs: SharedPreferences

    private var itemId: String? = null

    private lateinit var createdDate: OffsetDateTime

    internal constructor(itemId: String, dataSourceImpl: DataSourceImpl, mView: EditorNoteContract.View) {
        this.dataSourceImpl = dataSourceImpl
        this.mView = mView
        this.itemId = itemId

        mView.setPresenter(this)
    }

    internal constructor(prefs: SharedPreferences,
                         dataSourceImpl: DataSourceImpl,
                         mView: EditorNoteContract.View) {
        this.dataSourceImpl = dataSourceImpl
        this.mView = mView
        this.prefs = prefs

        mView.setPresenter(this)
    }

    override fun start() {
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
        }
        else {
            val note = Notes(
                    title,
                    password,
                    message,
                    textColor,
                    noteColor)

            createNote(note)
        }

    }

    private fun showNewNote() {
        val noteColor = prefs.getString("default_note_color", "#ef5350")
        val textColor = prefs.getString("default_text_color", "#000000")
        mView.noteColor(Color.parseColor(noteColor))
        mView.textColor(Color.parseColor(textColor))
    }

    override fun passwordDialog() {
        mView.showPasswordDialog()
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

    private fun createNote(note: Notes) {
        dataSourceImpl.saveNote(note)
        mView.showToastMessage("Note created")
    }

    private fun updateNote(note: Notes) {
        dataSourceImpl.updateNote(note)
        mView.showToastMessage("Note updated")
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
