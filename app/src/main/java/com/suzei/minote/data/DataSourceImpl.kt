package com.suzei.minote.data

import com.suzei.minote.data.dao.NotesDao
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.utils.executors.AppExecutor

class DataSourceImpl private constructor(
        private val appExecutor: AppExecutor,
        private val notesDao: NotesDao) : DataSource {

    companion object {

        fun getInstance(appExecutor: AppExecutor, notesDao: NotesDao): DataSourceImpl {
            return DataSourceImpl(appExecutor, notesDao)
        }

    }

    override fun updateNote(note: Notes) {
        val runnable = { notesDao.updateNote(note) }
        appExecutor.diskIO.execute(runnable)
    }

    override fun saveNote(note: Notes) {
        val runnable = { notesDao.saveNote(note) }
        appExecutor.diskIO.execute(runnable)
    }

    override fun deleteNote(note: Notes) {
        val runnable = { notesDao.deleteNote(note) }
        appExecutor.diskIO.execute(runnable)
    }

    override fun getNote(itemId: Int, listener: DataSource.NoteListener) {
        val runnable = {
            val note = notesDao.findNoteById(itemId)

            appExecutor.mainThread.execute {
                listener.onDataAvailable(note)
            }
        }

        appExecutor.diskIO.execute(runnable)
    }

    override fun getListOfNotes(listener: DataSource.ListNoteListener) {
        val runnable = {

            val listOfNotes = notesDao.findAllNotes()

            appExecutor.mainThread.execute {

                if (listOfNotes.isEmpty()) {
                    listener.onDataUnavailable()
                } else {
                    listener.onDataAvailable(listOfNotes.toMutableList())
                }

            }

        }

        appExecutor.diskIO.execute(runnable)
    }

}
