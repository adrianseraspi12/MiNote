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
                NoteEntry.TITLE,
                NoteEntry.PASSWORD,
                NoteEntry.COLOR,
                NoteEntry.TEXT_COLOR,
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
            String title = cursor.getString(cursor.getColumnIndex(NoteEntry.TITLE));
            String password = cursor.getString(cursor.getColumnIndex(NoteEntry.PASSWORD));
            String message = cursor.getString(cursor.getColumnIndex(NoteEntry.MESSAGE));
            String color = cursor.getString(cursor.getColumnIndex(NoteEntry.COLOR));
            String textColor = cursor.getString(cursor.getColumnIndex(NoteEntry.TEXT_COLOR));

            if (TodoJson.isValidJson(message)) {
                message = TodoJson.getMapFormatListString(message);
            }

            callbacks.finishLoad(title, password, message, color, textColor);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        callbacks.resetLoad();
    }

    public interface NoteCallbacks {

        void finishLoad(String title, String password, String message,
                        String color, String textColor);

        void resetLoad();
    }
}
