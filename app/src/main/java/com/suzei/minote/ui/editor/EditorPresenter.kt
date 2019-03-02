package com.suzei.minote.ui.editor

import android.content.SharedPreferences
import android.graphics.Color

import com.suzei.minote.data.DataSource
import com.suzei.minote.data.DataSourceImpl
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.utils.ColorWheel

class EditorPresenter : EditorContract.Presenter {

    private var dataSourceImpl: DataSource

    private var mView: EditorContract.View

    private lateinit var prefs: SharedPreferences

    private var itemId: Int = 0

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
        this.itemId = -1

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
        val note: Notes = if (itemId != -1) {

            Notes(itemId, title, password, message, textColor, noteColor)

        }
        else {
            Notes(title, password, message, textColor, noteColor)
        }

        saveNote(note)
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

    private fun saveNote(note: Notes) {
        dataSourceImpl.saveNote(note)
        mView.showNoteSave()
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
