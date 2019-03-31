package com.suzei.minote.data

import com.suzei.minote.data.entity.Notes
import com.suzei.minote.data.entity.Todo

interface DataSource {

    interface NoteListener {

        fun onDataAvailable(note: Notes)

        fun onDataUnavailable()

    }

    interface ListNoteListener {

        fun onDataAvailable(listOfNote: MutableList<Notes>)

        fun onDataUnavailable()

    }

    interface TodoListener {

        fun onDataAvailable(todo: Todo)

        fun onDataUnavailable()

    }

    fun saveNote(note: Notes)

    fun updateNote(note: Notes)

    fun getNote(itemId: Int, listener: NoteListener)

    fun getListOfNotes(listener: ListNoteListener)

    fun deleteNote(note: Notes)

}
