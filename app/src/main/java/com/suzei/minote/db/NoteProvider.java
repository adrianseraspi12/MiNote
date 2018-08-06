package com.suzei.minote.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.suzei.minote.db.NoteContract.NoteEntry;

public class NoteProvider extends ContentProvider {

    private static final String TAG = "NoteProvider";

    private NoteDBHelper mDBHelper;

    public static final int NOTES = 100;
    public static final int NOTES_ID = 101;

    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // getting multiple rows
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTES, NOTES);

        // getting single row
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTES + "/#",
                NOTES_ID);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new NoteDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {

            case NOTES:
                cursor = db.query(NoteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case NOTES_ID:
                if (selection == null) {
                    selection = NoteEntry._ID  + "=?";
                }
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(NoteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;

            default:
                throw new IllegalArgumentException("Cannot query, unknown URI -> " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case NOTES:
                return NoteEntry.CONTENT_LIST_TYPE;
            case NOTES_ID:
                return NoteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri = " + uri + " with match = " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        getContext().getContentResolver().notifyChange(uri, null);

        switch (match) {

            case NOTES:
                return insertNote(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported on " + uri);
        }
    }

    @Nullable
    private Uri insertNote(@NonNull Uri uri, @Nullable ContentValues values) {
        // Get writable database
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        sanityCheck(values);

        // Insert the new note for given values
        long id = db.insert(NoteEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(TAG, "insertNote: Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        getContext().getContentResolver().notifyChange(uri, null);

        switch (match) {

            case NOTES:
                return db.delete(NoteEntry.TABLE_NAME, null, null);

            case NOTES_ID:
                selection = NoteEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported on " + uri);
        }
    }



    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case NOTES:
                return updateNote(uri, values, selection, selectionArgs);

            case NOTES_ID:
                selection = NoteEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateNote(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateNote(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        if (values.size() == 0) {
            return 0;
        }

        if (values.size() > 1) {
            sanityCheck(values);
        }

        int id = db.update(NoteEntry.TABLE_NAME, values, selection, selectionArgs);

        if (id != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        if (id == -1) {
            Log.e(TAG, "updateNote: Failed to update row " + uri );
        }

        return id;
    }

    private void sanityCheck(ContentValues values) {
        Integer type = values.getAsInteger(NoteEntry.TYPE);
        if (type == null || !NoteEntry.isValidType(type)) {
            throw new IllegalArgumentException("Note requires a type");
        }

        String title = values.getAsString(NoteEntry.TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Note requires a title");
        }

        String color = values.getAsString(NoteEntry.COLOR);
        if (color == null) {
            throw new IllegalArgumentException("Note requires a color");
        }
    }
}
