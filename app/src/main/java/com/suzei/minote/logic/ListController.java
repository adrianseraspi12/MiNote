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

import com.suzei.minote.data.DataSource;
import com.suzei.minote.data.NoteContract.NoteEntry;
import com.suzei.minote.utils.Turing;
import com.suzei.minote.view.NotesView;
import com.suzei.minote.view.PasswordDialog;

public class ListController implements NoteListener {

    private NotesView notesView;
    private AppCompatActivity activity;
    private DataSource dataSource;

    public ListController(AppCompatActivity activity, NotesView notesView) {
        this.notesView = notesView;
        this.activity = activity;

        dataSource = new DataSource(activity, NoteEntry.CONTENT_URI, this);
    }

    public void init() {
        dataSource.initLoaderManager();
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

    @Override
    public void finished(Cursor cursor) {
        notesView.setDataToAdapter(cursor);
    }

    @Override
    public void reset() {
        notesView.resetLoader();
    }
}
