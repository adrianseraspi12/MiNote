package com.suzei.minote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
        try {
            createTable(db, "Temptable");
            copy(db, "Temptable", NoteEntry.TABLE_NAME);
            dropTable(db, NoteEntry.TABLE_NAME);
            createTable(db, NoteEntry.TABLE_NAME);
            copy(db, NoteEntry.TABLE_NAME, "Temptable");
            dropTable(db, "Temptable");
        } catch (SQLiteException e) {
            if (e.getMessage().contains("no such table")) {
                createTable(db, NoteEntry.TABLE_NAME);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(db);
    }

    private void createTable(SQLiteDatabase db, String tableName) {
        String SQL_CREATE_TEMP_TABLE = "CREATE TEMPORARY TABLE " + tableName
                + " (" + NoteEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + NoteEntry.TYPE + " INTEGER NOT NULL, "
                + NoteEntry.DATE + " TEXT, "
                + NoteEntry.TIME + " TEXT, "
                + NoteEntry.MESSAGE + " TEXT, "
                + NoteEntry.LOCATION + " TEXT, "
                + NoteEntry.COLOR + " TEXT);";
        db.execSQL(SQL_CREATE_TEMP_TABLE);
    }

    private void copy(SQLiteDatabase db, String intoTable, String fromTable) {
        String SQL_INSERT_NOTES_INTO_TEMPTABLE = "INSERT INTO " + intoTable
                + " SELECT " + NoteEntry._ID + ", "
                + NoteEntry.TYPE + ", "
                + NoteEntry.DATE + ", "
                + NoteEntry.TIME + ", "
                + NoteEntry.MESSAGE + ", "
                + NoteEntry.LOCATION + ", "
                + NoteEntry.COLOR
                + " FROM " + fromTable;
        db.execSQL(SQL_INSERT_NOTES_INTO_TEMPTABLE);
    }

    private void dropTable(SQLiteDatabase db, String tableName) {
        String dropTable = "DROP TABLE " + tableName;
        db.execSQL(dropTable);
    }

}
