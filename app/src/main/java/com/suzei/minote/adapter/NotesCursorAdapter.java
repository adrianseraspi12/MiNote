package com.suzei.minote.adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.suzei.minote.utils.JsonConvert;
import com.suzei.minote.utils.Turing;
import com.suzei.minote.view.EditorActivity;
import com.suzei.minote.view.PasswordDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesCursorAdapter extends CursorRecyclerviewAdapter<NotesCursorAdapter.ViewHolder> {

    private final Activity activity;

    public NotesCursorAdapter(Activity activity, Cursor cursor) {
        super(cursor);
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(NotesCursorAdapter.ViewHolder viewHolder, Cursor cursor) {
        Notes note = get(cursor);
        viewHolder.bind(note);
    }

    @Override
    public ViewHolder onCreateVH(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_notes_default,
                parent, false);
        return new NotesCursorAdapter.ViewHolder(view);
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

            if (JsonConvert.isValidJson(note.getMessage())) {
                String str = JsonConvert.getMapFormatListString(note.getMessage());
                messageView.setText(str);
            } else {
                messageView.setText(note.getTitle());
            }

            if (note.getPassword() != null) {
                passwordView.setVisibility(View.VISIBLE);
            }

            deleteView.setOnClickListener(v -> deleteNote(note.get_id()));

            itemView.setOnClickListener(v -> {

                if (note.getPassword() != null) {
                    showEnterPassword(note.get_id(), note.getPassword());
                } else {
                    startEditorActivity(note.get_id());
                }

            });
        }

        private void showEnterPassword(final int id, final String password) {
            PasswordDialog passwordDialog = new PasswordDialog(activity);
            passwordDialog.show();
            passwordDialog.setOnClosePasswordDialog(enteredPassword -> {

                if (!Turing.decrypt(password).equals(enteredPassword)) {
                    Toast.makeText(activity,
                            R.string.wrong_password,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    startEditorActivity(id);
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
            builder.setMessage(R.string.delete_note_message);

            builder.setPositiveButton(R.string.delete, (dialog, which) -> {

                Uri noteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
                int deleteNoteRow = activity.getContentResolver().
                        delete(noteUri, null, null);

                if (deleteNoteRow != 0) {
                    Toast.makeText(activity,
                            R.string.note_deleted,
                            Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

            builder.show();
        }

    }

}
