package com.suzei.minote

import android.content.Context

import com.suzei.minote.data.DataSourceImpl
import com.suzei.minote.data.NotesDatabase
import com.suzei.minote.utils.executors.AppExecutor

object Injection {

    fun provideDataSourceImpl(context: Context): DataSourceImpl {
        val notesDatabase = NotesDatabase.getInstance(context)

        return DataSourceImpl.getInstance(
                AppExecutor.instance,
                notesDatabase.notesDao())
    }

}
