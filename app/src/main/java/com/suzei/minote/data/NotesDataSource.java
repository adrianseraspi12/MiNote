package com.suzei.minote.data;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.suzei.minote.logic.NoteListener;

import static com.suzei.minote.data.NoteContract.NoteEntry;

public class NotesDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NOTE_LOADER = 0;

    private AppCompatActivity activity;
    private Uri uri;

    private NoteListener noteListener;

    public NotesDataSource(AppCompatActivity activity, Uri uri, NoteListener noteListener) {
        this.activity = activity;
        this.uri = uri;
        this.noteListener = noteListener;
    }

    public void initLoaderManager() {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        loaderManager.initLoader(NOTE_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        String[] projection = {NoteEntry._ID,
                NoteEntry.TITLE,
                NoteEntry.PASSWORD,
                NoteEntry.COLOR,
                NoteEntry.TEXT_COLOR,
                NoteEntry.MESSAGE};

        return new CursorLoader(
                activity,
                uri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        noteListener.finished(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        noteListener.reset();
    }

}
