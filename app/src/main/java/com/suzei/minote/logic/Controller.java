package com.suzei.minote.logic;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.suzei.minote.data.NotesDataSource;
import com.suzei.minote.view.NotesView;

public class Controller implements NoteListener {

    private NotesView notesView;
    private NotesDataSource notesDataSource;

    public Controller(AppCompatActivity activity, Uri uri, NotesView notesView) {
        this.notesView = notesView;

        notesDataSource = new NotesDataSource(activity, uri, this);
    }

    public void init() {
        notesDataSource.initLoaderManager();
    }

    @Override
    public void finished(Cursor cursor) {
        notesView.showDataToUi(cursor);
    }

    @Override
    public void reset() {
        notesView.resetLoader();
    }
}
