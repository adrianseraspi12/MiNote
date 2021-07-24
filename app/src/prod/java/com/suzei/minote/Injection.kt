package com.suzei.minote

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.suzei.minote.data.local.NotesDatabase
import com.suzei.minote.data.local.service.NoteLocalService
import com.suzei.minote.data.local.service.TodoLocalService
import com.suzei.minote.data.repository.NoteDataSource
import com.suzei.minote.data.repository.TodoDataSource
import kotlinx.coroutines.Dispatchers.IO

object Injection {
    fun provideSharedPreference(activity: Activity): SharedPreferences {
        return activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        )
    }

    fun provideNoteDataSource(context: Context): NoteDataSource {
        val database = getDatabase(context)
        val noteLocalService = NoteLocalService.instance(IO, database.notesDao())
        return NoteDataSource.instance(noteLocalService)
    }

    fun provideTodoDataSource(context: Context): TodoDataSource {
        val database = getDatabase(context)
        val todoLocalService = TodoLocalService.instance(IO,
                database.todoDao(),
                database.todoItemDao())
        return TodoDataSource.instance(todoLocalService)
    }

    private fun getDatabase(context: Context): NotesDatabase {
        return NotesDatabase.getInstance(context)
    }
}
