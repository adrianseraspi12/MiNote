package com.suzei.minote

import android.content.Context
import com.suzei.minote.data.NotesDatabase
import com.suzei.minote.data.repository.NotesRepository
import com.suzei.minote.data.repository.TodoRepository
import com.suzei.minote.utils.executors.AppExecutor

object Injection {

    fun provideNotesRepository(context: Context): NotesRepository {
        return NotesRepository.getInstance(
                AppExecutor.instance,
                getDatabase(context).notesDao())
    }

    fun provideTodoRepository(context: Context): TodoRepository {
        val database = getDatabase(context)

        return TodoRepository.getInstance(
                AppExecutor.instance,
                database.todoDao(),
                database.todoItemDao())
    }

    private fun getDatabase(context: Context): NotesDatabase {
        return NotesDatabase.getInstance(context)
    }

}
