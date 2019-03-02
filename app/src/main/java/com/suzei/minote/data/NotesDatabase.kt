package com.suzei.minote.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.suzei.minote.data.dao.NotesDao
import com.suzei.minote.data.entity.Notes

//  If the database updated, Increment version by 1
@Database(entities = [Notes::class], version = 4)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {

        private val DATABASE_NAME = "notes_storage.db"

        private val sLock = Any()

        fun getInstance(context: Context): NotesDatabase {
            synchronized(sLock) {
                return Room.databaseBuilder(
                        context.applicationContext,
                        NotesDatabase::class.java,
                        DATABASE_NAME)
                        .addMigrations(MIGRATION_2_3)
                        .build()
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {

            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }

        }

    }

}
