package com.suzei.minote.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.suzei.minote.data.NoteContract.NoteEntry;

public class NoteDBHelper extends SQLiteOpenHelper {

    // if you change the database schema, then increment it by 1
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "notes_storage.db";

    NoteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME
                + " (" + NoteEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + NoteEntry.TITLE + " TEXT, "
                + NoteEntry.PASSWORD + " TEXT, "
                + NoteEntry.MESSAGE + " TEXT, "
                + NoteEntry.COLOR + " TEXT, "
                + NoteEntry.TEXT_COLOR + " TEXT);";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                createTempTable(db);
                copy(db, "Temptable", NoteEntry.TABLE_NAME);
                dropTable(db, NoteEntry.TABLE_NAME);
                createTable(db, NoteEntry.TABLE_NAME);
                copy(db, NoteEntry.TABLE_NAME, "Temptable");
                dropTable(db, "Temptable");
                addColumn(db, NoteEntry.TEXT_COLOR);
                addColumn(db, NoteEntry.PASSWORD);
                break;
            default:
                throw new IllegalArgumentException("Invalid oldVersion= " + oldVersion);
        }
    }

    private void createTable(SQLiteDatabase db, String tableName) {
        String SQL_CREATE_TEMP_TABLE = "CREATE TABLE " + tableName
                + " (" + NoteEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + NoteEntry.TITLE + " TEXT, "
                + NoteEntry.MESSAGE + " TEXT, "
                + NoteEntry.COLOR + " TEXT);";
        db.execSQL(SQL_CREATE_TEMP_TABLE);
    }

    private void createTempTable(SQLiteDatabase db) {
        String SQL_CREATE_TEMP_TABLE = "CREATE TEMPORARY TABLE " + "Temptable"
                + " (" + NoteEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + NoteEntry.TITLE + " TEXT, "
                + NoteEntry.MESSAGE + " TEXT, "
                + NoteEntry.COLOR + " TEXT);";
        db.execSQL(SQL_CREATE_TEMP_TABLE);
    }

    private void copy(SQLiteDatabase db, String intoTable, String fromTable) {
        String SQL_INSERT_NOTES_INTO_TEMPTABLE = "INSERT INTO " + intoTable
                + " SELECT " + NoteEntry._ID + ", "
                + NoteEntry.TITLE + ", "
                + NoteEntry.MESSAGE + ", "
                + NoteEntry.COLOR
                + " FROM " + fromTable;
        db.execSQL(SQL_INSERT_NOTES_INTO_TEMPTABLE);
    }

    private void dropTable(SQLiteDatabase db, String tableName) {
        String dropTable = "DROP TABLE " + tableName;
        db.execSQL(dropTable);
    }

    private void addColumn(SQLiteDatabase db, String columnName) {
        String addColumn = "ALTER TABLE " + NoteEntry.TABLE_NAME + " ADD COLUMN "
                + columnName + " TEXT";
        db.execSQL(addColumn);
    }

}
