package com.suzei.minote.data.repository

import com.suzei.minote.data.dao.NotesDao
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.utils.executors.AppExecutor

class NotesRepository private constructor(
        private val appExecutor: AppExecutor,
        private val notesDao: NotesDao) : Repository<Notes> {

    companion object {

        fun getInstance(appExecutor: AppExecutor, notesDao: NotesDao): NotesRepository {
            return NotesRepository(appExecutor, notesDao)
        }

    }

    override fun save(data: Notes, actionListener: Repository.ActionListener) {
        val runnable = {
            notesDao.saveNote(data)

            appExecutor.mainThread.execute {
                actionListener.onSuccess(data.id!!, data.createdDate!!)
            }

        }
        appExecutor.diskIO.execute(runnable)
    }

    override fun update(data: Notes) {
        val runnable = { notesDao.updateNote(data) }
        appExecutor.diskIO.execute(runnable)
    }

    override fun getData(itemId: String, listener: Repository.Listener<Notes>) {
        val runnable = {
            val note = notesDao.findNoteById(itemId)

            appExecutor.mainThread.execute {
                listener.onDataAvailable(note)
            }
        }

        appExecutor.diskIO.execute(runnable)
    }

    override fun getListOfData(listener: Repository.ListListener<Notes>) {
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

    override fun delete(data: Notes) {
        val runnable = { notesDao.deleteNote(data) }
        appExecutor.diskIO.execute(runnable)
    }
}