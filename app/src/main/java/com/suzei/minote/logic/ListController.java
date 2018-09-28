package com.suzei.minote.logic;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.utils.Turing;
import com.suzei.minote.view.NotesView;
import com.suzei.minote.view.PasswordDialog;

public class ListController implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NOTE_LOADER = 0;

    private NotesView notesView;
    private AppCompatActivity activity;

    public ListController(AppCompatActivity activity, NotesView notesView) {
        this.notesView = notesView;
        this.activity = activity;
        initLoaderManager();
    }

    private void initLoaderManager() {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        loaderManager.initLoader(NOTE_LOADER, null, this);
    }

    public void onItemClick(String uriString, String password) {
        if (password != null) {
            showPasswordDialog(uriString, password);
        } else {
            notesView.startActivity(uriString);
        }

    }

    private void showPasswordDialog(final String uriString, final String password) {
        PasswordDialog passwordDialog = new PasswordDialog(activity);
        passwordDialog.show();
        passwordDialog.setOnClosePasswordDialog(new PasswordDialog.PasswordDialogListener() {

            @Override
            public void onClose(String enteredPassword) {

                if (!Turing.decrypt(password).equals(enteredPassword)) {
                    notesView.onPasswordisInvalid();
                } else {
                    notesView.startActivity(uriString);
                }

            }
        });
    }

    public void showDeleteConfirmationDialog(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure want to delete this note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri noteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
                int deleteNoteRow = activity.getContentResolver().delete(noteUri, null,
                        null);

                if (deleteNoteRow != 0) {
                    notesView.onDeleteSuccess();
                } else {
                    notesView.onDeleteFailed();
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri uri = NoteEntry.CONTENT_URI;

        String[] projection = {
                NoteEntry._ID,
                NoteEntry.TITLE,
                NoteEntry.PASSWORD,
                NoteEntry.MESSAGE,
                NoteEntry.COLOR,
                NoteEntry.TEXT_COLOR };

        return new CursorLoader(activity, uri, projection, null, null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        notesView.setDataToAdapter(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        notesView.resetLoader();
    }
}
