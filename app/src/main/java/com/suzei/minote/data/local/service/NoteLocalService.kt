package com.suzei.minote.data.local.service

import com.suzei.minote.data.Result
import com.suzei.minote.data.local.dao.NotesDao
import com.suzei.minote.data.local.entity.Notes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class NoteLocalService(
        private val ioDispatcher: CoroutineDispatcher = IO,
        private val notesDao: NotesDao) : LocalService<Notes> {

    companion object {
        fun instance(ioDispatcher: CoroutineDispatcher, notesDao: NotesDao):
                NoteLocalService = NoteLocalService(ioDispatcher, notesDao)
    }

    override suspend fun save(data: Notes): Result<Notes> = withContext(ioDispatcher) {
        return@withContext try {
            notesDao.saveNote(data)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun update(data: Notes): Result<Nothing> = withContext(ioDispatcher) {
        return@withContext try {
            notesDao.updateNote(data)
            Result.Success()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getData(itemId: String): Result<Notes> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(notesDao.findNoteById(itemId))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getListOfData(): Result<List<Notes>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(notesDao.findAllNotes())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun delete(data: Notes): Result<Nothing> = withContext(ioDispatcher) {
        return@withContext try {
            notesDao.deleteNote(data)
            Result.Success()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}