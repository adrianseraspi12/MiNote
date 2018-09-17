package com.suzei.minote.adapter;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.suzei.minote.R;
import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.models.Notes;
import com.suzei.minote.utils.FullScreenFragmentSelector;

public class NotesAdapter extends CursorAdapter {

    private static final String TAG = "NotesAdapter";
    private static final int TYPE_TODO = 0;
    private static final int TYPE_DEFAULT = 1;

    private Context mContext;

    private DatabaseCallbacks callbacks;

    public NotesAdapter(Context context, Cursor c, DatabaseCallbacks callbacks) {
        super(context, c, 0);
        this.mContext = context;
        this.callbacks = callbacks;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        int type = cursor.getInt(cursor.getColumnIndex(NoteEntry.TYPE));

        switch (type) {
            case NoteEntry.TYPE_TODO:
                return TYPE_TODO;
            default:
                return TYPE_DEFAULT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        if (getItemViewType(cursor.getPosition()) == TYPE_TODO) {
            return LayoutInflater.from(context).inflate(R.layout.item_row_notes_todo, parent,
                    false);
        } else {
            return LayoutInflater.from(context).inflate(R.layout.item_row_notes_default, parent,
                    false);
        }
    }

    @Override
    public void bindView(final View view, Context context, final Cursor cursor) {
        int position = cursor.getPosition();
        Notes notes = get(position, cursor);
        ViewHolder holder = new ViewHolder(view);

        Log.d(TAG, "bindView: message=" + notes.getMessage());
        holder.messageView.setText(notes.getMessage());
        holder.colorNote.setBackgroundColor(Color.parseColor(notes.getColor()));

        Bundle bundle = new Bundle();

        bundle.putInt("note_id", notes.get_id());
        bundle.putInt("note_type", notes.getType());
        bundle.putString("note_date", notes.getDate());
        bundle.putString("note_time", notes.getTime());
        bundle.putString("note_message", notes.getMessage());
        bundle.putString("note_color", notes.getColor());

    }

    private Notes get(int position, Cursor cursor) {
        Notes notes = new Notes();

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
                callbacks.AfterDeletion();
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

    public class ViewHolder {
        View view;
        View colorNote;
        TextView messageView;

        ViewHolder(View view) {
            this.view = view;
            colorNote = view.findViewById(R.id.item_notes_color);
            messageView = view.findViewById(R.id.item_notes_message);
        }
    }

    public interface DatabaseCallbacks {
        void AfterDeletion();
    }
}
