package com.suzei.minote.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suzei.minote.R;
import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.models.Notes;

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

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Notes note) {
            colorNote.setBackgroundColor(Color.parseColor(note.getColor()));
            messageView.setText(note.getMessage());
        }

    }
}
