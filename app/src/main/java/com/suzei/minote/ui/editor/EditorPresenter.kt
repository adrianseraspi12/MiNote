package com.suzei.minote.ui.editor

import android.content.SharedPreferences
import android.graphics.Color

import com.suzei.minote.data.DataSource
import com.suzei.minote.data.DataSourceImpl
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.utils.ColorWheel
import com.suzei.minote.utils.LogMe

class EditorPresenter : EditorContract.Presenter {

    private var dataSourceImpl: DataSource

    private var mView: EditorContract.View

    private lateinit var prefs: SharedPreferences

    private var itemId: Int = -1

    internal constructor(itemId: Int, dataSourceImpl: DataSourceImpl, mView: EditorContract.View) {
        this.dataSourceImpl = dataSourceImpl
        this.mView = mView
        this.itemId = itemId

        mView.setPresenter(this)
    }

    internal constructor(prefs: SharedPreferences,
                         dataSourceImpl: DataSourceImpl,
                         mView: EditorContract.View) {
        this.dataSourceImpl = dataSourceImpl
        this.mView = mView
        this.prefs = prefs

        mView.setPresenter(this)
    }

    override fun start() {
        if (itemId != -1) {
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

        if (itemId == -1) {
            val note = Notes(
                    title,
                    password,
                    message,
                    textColor,
                    noteColor)

            createNote(note)
        }
        else {
            val note = Notes(
                    itemId,
                    title,
                    password,
                    message,
                    textColor,
                    noteColor)

            updateNote(note)
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
        dataSourceImpl.getNote(itemId, object : DataSource.NoteListener {

            override fun onDataAvailable(note: Notes) {
                mView.showNoteDetails(note)
            }

            override fun onDataUnavailable() {

            }

        })
    }

}
