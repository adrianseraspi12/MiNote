package com.suzei.minote.data

import android.content.ContentValues
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.suzei.minote.data.local.converter.DateTimeTypeConverter
import com.suzei.minote.data.local.dao.NotesDao
import com.suzei.minote.data.local.dao.TodoDao
import com.suzei.minote.data.local.dao.TodoItemDao
import com.suzei.minote.data.local.entity.Notes
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.local.entity.TodoItem
import com.suzei.minote.utils.LogMe

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
                        .addMigrations(MIGRATION_2_3, MIGRATION_4_5, MIGRATION_3_6)
                        .build()
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {

            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }

        }

        private val MIGRATION_4_5: Migration = object : Migration(4, 5) {

            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }

        }

        private val MIGRATION_3_6: Migration = object : Migration(3, 6) {

            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    val c = database.query("SELECT * FROM notes")
                    c.use {

                        if (c.moveToFirst()) {

                            val cv = ContentValues()
                            cv.put("_id", c.getInt(c.getColumnIndex("_id")))
                            cv.put("password", c.getString(c.getColumnIndex("password")))
                            cv.put("title", c.getString(c.getColumnIndex("title")))
                            cv.put("message", c.getString(c.getColumnIndex("message")))
                            cv.put("color", c.getString(c.getColumnIndex("color")))
                            cv.put("text_color", c.getString(c.getColumnIndex("text_color")))

                            database.execSQL("DROP TABLE IF EXISTS `notes`")
                            createNotesTable(database)
                            database.insert("notes", 0, cv)

                        } else {

                            database.execSQL("DROP TABLE IF EXISTS `customer`")
                            createNotesTable(database)

                        }

                    }

                } catch (e: Exception) {
                    LogMe.info("[DATABASE ROOM EXCEPTION] ${e.message}")
                }
            }

        }

        fun createNotesTable(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE IF NOT EXISTS `notes` (
                            `_id` TEXT NOT NULL PRIMARY KEY,
                            `created_date` TEXT,
                            `text_color` TEXT,
                            `color` TEXT,
                            `message` TEXT,
                            `title` TEXT,
                            `password` TEXT)""".trimIndent())
        }

    }

}
