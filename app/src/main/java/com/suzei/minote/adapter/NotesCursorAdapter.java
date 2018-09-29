package com.suzei.minote.adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suzei.minote.R;
import com.suzei.minote.data.NoteContract.NoteEntry;
import com.suzei.minote.models.Notes;
import com.suzei.minote.utils.TodoJson;
import com.suzei.minote.utils.Turing;
import com.suzei.minote.view.EditorActivity;
import com.suzei.minote.view.PasswordDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesCursorAdapter extends CursorRecyclerviewAdapter<NotesCursorAdapter.ViewHolder> {

    private Activity activity;

    public NotesCursorAdapter(Activity activity, Cursor cursor) {
        super(activity, cursor);
        this.activity = activity;
    }

    @Override
    public NotesCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_notes_default,
                parent, false);
        return new NotesCursorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesCursorAdapter.ViewHolder viewHolder, Cursor cursor) {
        Notes note = get(cursor);
        viewHolder.bind(note);
    }

    private Notes get(Cursor cursor) {
        Notes notes = new Notes();
        int position = cursor.getPosition();

        if (cursor.moveToPosition(position)) {
            int _id = cursor.getInt(cursor.getColumnIndex(NoteEntry._ID));
            String message = cursor.getString(cursor.getColumnIndex(NoteEntry.MESSAGE));
            String title = cursor.getString(cursor.getColumnIndex(NoteEntry.TITLE));
            String password = cursor.getString(cursor.getColumnIndex(NoteEntry.PASSWORD));
            String color = cursor.getString(cursor.getColumnIndex(NoteEntry.COLOR));

            notes.set_id(_id);
            notes.setMessage(message);
            notes.setColor(color);
            notes.setTitle(title);
            notes.setPassword(password);
        }
        return notes;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_notes_color)
        View colorNote;
        @BindView(R.id.item_notes_title)
        TextView messageView;
        @BindView(R.id.item_notes_delete)
        ImageButton deleteView;
        @BindView(R.id.item_notes_password)
        ImageView passwordView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Notes note) {
            colorNote.setBackgroundColor(Color.parseColor(note.getColor()));

            if (TodoJson.isValidJson(note.getMessage())) {
                String str = TodoJson.getMapFormatListString(note.getMessage());
                messageView.setText(str);
            } else {
                messageView.setText(note.getTitle());
            }

            if (note.getPassword() != null) {
                passwordView.setVisibility(View.VISIBLE);
            }

            deleteView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    deleteNote(note.get_id());
                }

            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (note.getPassword() != null) {
                        showEnterPassword(note.get_id(), note.getPassword());
                    } else {
                        startEditorActivity(note.get_id());
                    }

                }
            });
        }

        private void showEnterPassword(final int id, final String password) {
            PasswordDialog passwordDialog = new PasswordDialog(activity);
            passwordDialog.show();
            passwordDialog.setOnClosePasswordDialog(new PasswordDialog.PasswordDialogListener() {

                @Override
                public void onClose(String enteredPassword) {

                    if (!Turing.decrypt(password).equals(enteredPassword)) {
                        Toast.makeText(activity,
                                "Wrong Password",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        startEditorActivity(id);
                    }

                }
            });
        }

        private void startEditorActivity(int id) {
            Uri uri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
            String uriString = String.valueOf(uri);
            Intent intent = new Intent(activity, EditorActivity.class);
            intent.putExtra(EditorActivity.EXTRA_NOTE_URI, uriString);
            activity.startActivity(intent);
        }

        private void deleteNote(final int id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Are you sure want to delete this note?");

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri noteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
                    int deleteNoteRow = activity.getContentResolver().
                            delete(noteUri, null, null);

                    if (deleteNoteRow != 0) {
                        Toast.makeText(activity,
                                "Note Deleted",
                                Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        }

    }

}
