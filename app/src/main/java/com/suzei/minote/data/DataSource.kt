package com.suzei.minote.data

import com.suzei.minote.data.entity.Notes
import org.threeten.bp.OffsetDateTime

interface DataSource {

    interface NoteListener {

        fun onDataAvailable(note: Notes)

        fun onDataUnavailable()

    }

    interface ListNoteListener {

        fun onDataAvailable(listOfNote: MutableList<Notes>)

        fun onDataUnavailable()

    }

    interface ActionListener {

        fun onSuccess(itemId: String, createdDate: OffsetDateTime)

        fun onFailed()

    }

    fun saveNote(note: Notes, actionListener: ActionListener)

    fun updateNote(note: Notes)

    fun getNote(itemId: String, listener: NoteListener)

    fun getListOfNotes(listener: ListNoteListener)

    fun deleteNote(note: Notes)

}
