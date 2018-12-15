package com.suzei.minote.data;

import android.content.Context;

import com.suzei.minote.data.dao.NotesDao;
import com.suzei.minote.data.entity.Notes;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

//  If the database updated, Increment version by 1
@Database(entities = Notes.class, version = 3)
public abstract class NotesDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "notes_storage.db";

    private static NotesDatabase sInstance;

    public abstract NotesDao notesDao();

    private static final Object sLock = new Object();

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }

    };

    public static NotesDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        NotesDatabase.class,
                        DATABASE_NAME)
                        .addMigrations(MIGRATION_2_3)
                        .build();
            }
            return sInstance;
        }
    }

}
