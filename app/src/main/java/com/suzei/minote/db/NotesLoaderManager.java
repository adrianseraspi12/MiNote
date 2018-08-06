package com.suzei.minote.db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.suzei.minote.utils.TodoJson;

import static com.suzei.minote.db.NoteContract.NoteEntry;

public class NotesLoaderManager implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "NotesLoaderManager";

    private Context context;
    private Uri mCurrentNoteUri;
    private NoteCallbacks callbacks;

    public NotesLoaderManager(Context context, Uri mCurrentNoteUri, NoteCallbacks callbacks) {
        this.context = context;
        this.mCurrentNoteUri = mCurrentNoteUri;
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {NoteEntry._ID,
                NoteEntry.TYPE,
                NoteEntry.COLOR,
                NoteEntry.TITLE,
                NoteEntry.DATE,
                NoteEntry.TIME,
                NoteEntry.LOCATION,
                NoteEntry.MESSAGE};

        return new CursorLoader(context,
                mCurrentNoteUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int type = cursor.getInt(cursor.getColumnIndex(NoteEntry.TYPE));
            String title = cursor.getString(cursor.getColumnIndex(NoteEntry.TITLE));
            String date = cursor.getString(cursor.getColumnIndex(NoteEntry.DATE));
            String time = cursor.getString(cursor.getColumnIndex(NoteEntry.TIME));
            String message = cursor.getString(cursor.getColumnIndex(NoteEntry.MESSAGE));
            String location = cursor.getString(cursor.getColumnIndex(NoteEntry.LOCATION));
            String color = cursor.getString(cursor.getColumnIndex(NoteEntry.COLOR));

            if (NoteEntry.TYPE_TODO == type) {
                message = TodoJson.getMapFormatListString(message);
            }

            callbacks.finishLoad(title, date, time, message, location, color);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        callbacks.resetLoad();
    }

    public interface NoteCallbacks {
        void finishLoad(String title, String date, String time, String message, String location,
                        String color);

        void resetLoad();
    }
}
