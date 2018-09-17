package com.suzei.minote.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.suzei.minote.R;
import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.models.Notes;
import com.suzei.minote.utils.FullScreenFragmentSelector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesCursorAdapter extends CursorRecyclerviewAdapter<NotesCursorAdapter.ViewHolder> {

    public NotesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_notes_default,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Notes note = get(cursor);
        viewHolder.bind(note);
    }

    private Notes get(Cursor cursor) {
        Notes notes = new Notes();
        int position = cursor.getPosition();

        if (cursor.moveToPosition(position)) {
            int _id = cursor.getInt(cursor.getColumnIndex(NoteEntry._ID));
            int type = cursor.getInt(cursor.getColumnIndex(NoteEntry.TYPE));
            String date = cursor.getString(cursor.getColumnIndex(NoteEntry.DATE));
            String time = cursor.getString(cursor.getColumnIndex(NoteEntry.TIME));
            String message = cursor.getString(cursor.getColumnIndex(NoteEntry.MESSAGE));
            String color = cursor.getString(cursor.getColumnIndex(NoteEntry.COLOR));

            notes.set_id(_id);
            notes.setType(type);
            notes.setDate(date);
            notes.setTime(time);
            notes.setMessage(message);
            notes.setColor(color);
        }
        return notes;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_notes_color) View colorNote;
        @BindView(R.id.item_notes_message) TextView messageView;
        @BindView(R.id.item_notes_delete) ImageButton deleteView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Notes note) {
            colorNote.setBackgroundColor(Color.parseColor(note.getColor()));
            messageView.setText(note.getMessage());
            setDeleteClickListener(note.get_id());
            setNoteClickListener(note.get_id(), note.getType());
        }

        private void setDeleteClickListener(final int id) {
            deleteView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog(id);
                }

            });
        }

        private void setNoteClickListener(final int id, final int type) {
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    editNote(id, type);
                }

            });
        }

        private void editNote(int id, int type) {
            Bundle bundle = new Bundle();
            Uri currentNoteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
            Class<?extends Fragment> fragment = FullScreenFragmentSelector.getFragmentClass(type);
            bundle.putString("note_uri", String.valueOf(currentNoteUri));

            FullScreenDialogFragment dialogFragment = new FullScreenDialogFragment.Builder(mContext)
                    .setTitle("Edit " + NoteEntry.getTypeString(type))
                    .setConfirmButton("Save")
                    .setContent(fragment, bundle)
                    .build();

            FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
            dialogFragment.show(fm, "dialog");
        }

        private void showDeleteConfirmationDialog(final int id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Are you sure want to delete this note?");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri noteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
                    int deleteNoteRow = mContext.getContentResolver().delete(noteUri, null,
                            null);
                    if (deleteNoteRow != 0) {
                        Toast.makeText(mContext, "Note deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Error deleting note", Toast.LENGTH_SHORT).show();
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

    }
}
