package com.suzei.minote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.suzei.minote.db.NoteContract.NoteEntry;

public class NoteDBHelper extends SQLiteOpenHelper {

    // if you change the database schema, then increment it by 1
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notes_storage.db";
    private static final String TAG = "NoteDBHelper";

    public NoteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME
                + " (" + NoteEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + NoteEntry.TYPE + " INTEGER NOT NULL, "
                + NoteEntry.TITLE + " TEXT NOT NULL, "
                + NoteEntry.DATE + " TEXT, "
                + NoteEntry.TIME + " TEXT, "
                + NoteEntry.MESSAGE + " TEXT, "
                + NoteEntry.LOCATION + " TEXT, "
                + NoteEntry.COLOR + " TEXT);";

        Log.d(TAG, "onCreate: SQL query-> " + SQL_CREATE_NOTES_TABLE);
        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(db);
    }
}
