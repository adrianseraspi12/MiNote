package com.suzei.minote.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.suzei.minote.data.converter.DateTimeTypeConverter
import com.suzei.minote.data.dao.NotesDao
import com.suzei.minote.data.dao.TodoDao
import com.suzei.minote.data.dao.TodoItemDao
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem

//  If the database updated, Increment version by 1
@Database(entities = [Notes::class, Todo::class, TodoItem::class], version = 6)
@TypeConverters(DateTimeTypeConverter::class)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao
    abstract fun todoDao(): TodoDao
    abstract fun todoItemDao(): TodoItemDao

    companion object {

        private const val DATABASE_NAME = "notes_storage.db"

        private val sLock = Any()

        fun getInstance(context: Context): NotesDatabase {
            synchronized(sLock) {
                return Room.databaseBuilder(
                        context.applicationContext,
                        NotesDatabase::class.java,
                        DATABASE_NAME)
                        .addMigrations(MIGRATION_2_3, MIGRATION_4_5)
                        .build()
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {

            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }

        }

        private val MIGRATION_4_5: Migration = object: Migration(4, 5) {

            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }

        }

    }

}
