package com.suzei.minote.data;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;

import com.suzei.minote.logic.NoteListener;

import static com.suzei.minote.data.NoteContract.NoteEntry;

public class NotesDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NOTE_LOADER = 0;

    private final AppCompatActivity activity;
    private final Uri uri;

    private final NoteListener noteListener;

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
