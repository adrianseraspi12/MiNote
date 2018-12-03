package com.suzei.minote.ui.editor;

import android.content.SharedPreferences;
import android.graphics.Color;

import com.suzei.minote.data.DataSource;
import com.suzei.minote.data.DataSourceImpl;
import com.suzei.minote.data.entity.Notes;

public class EditorPresenter implements EditorContract.Presenter {

    private DataSourceImpl dataSourceImpl;

    private EditorContract.View mView;

    private SharedPreferences prefs;

    private int itemId;

    EditorPresenter(int itemId, DataSourceImpl dataSourceImpl, EditorContract.View mView) {
        this.dataSourceImpl = dataSourceImpl;
        this.mView = mView;
        this.itemId = itemId;

        mView.setPresenter(this);
    }

    EditorPresenter(SharedPreferences prefs,
                    DataSourceImpl dataSourceImpl,
                    EditorContract.View mView) {
        this.dataSourceImpl = dataSourceImpl;
        this.mView = mView;
        this.prefs = prefs;
        this.itemId = -1;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (itemId != -1) {
            showNote();
        } else {
            showNewNote();
        }
    }

    @Override
    public void saveNote(String title,
                         String message,
                         String noteColor,
                         String textColor,
                         String password) {
        Notes note;

        if (itemId != -1) {
            note = new Notes(itemId, title, password, message, textColor, noteColor);
        }
        else {
            note = new Notes(title, password, message, textColor, noteColor);
        }

        saveNote(note);
    }

    private void showNewNote() {
        String noteColor = prefs.getString("default_note_color", "#ef5350");
        String textColor = prefs.getString("default_text_color", "#000000");
        mView.noteColor(Color.parseColor(noteColor));
        mView.textColor(Color.parseColor(textColor));
    }

    @Override
    public void passwordDialog() {
        mView.showPasswordDialog();
    }

    @Override
    public void noteColorWheel(int initialColor) {
        mView.showColorWheel(
                "Choose note color",
                initialColor,
                color -> mView.noteColor(color));
    }

    @Override
    public void textColorWheel(int initialColor) {
        mView.showColorWheel(
                "Choose text color",
                initialColor,
                color -> mView.textColor(color));
    }

    private void saveNote(Notes note) {
        dataSourceImpl.saveNote(note);
        mView.showNoteSave();
    }

    private void showNote() {
        dataSourceImpl.getNote(itemId, new DataSource.Listener<Notes>() {

            @Override
            public void onDataAvailable(Notes result) {
                mView.showNoteDetails(result);
            }

            @Override
            public void onDataUnavailable() {

            }

        });
    }

}
