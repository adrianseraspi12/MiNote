package com.suzei.minote.view;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suzei.minote.R;
import com.suzei.minote.data.NoteContract.NoteEntry;
import com.suzei.minote.logic.ListController;
import com.suzei.minote.models.Notes;
import com.suzei.minote.preference.SettingsActivity;
import com.suzei.minote.utils.AppRater;
import com.suzei.minote.utils.RecyclerViewEmptySupport;
import com.suzei.minote.utils.TodoJson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity extends AppCompatActivity implements NotesView {

    private ListController controller;
    private NotesCursorAdapter adapter;

    @BindView(R.id.list_notes) RecyclerViewEmptySupport noteList;
    @BindView(R.id.list_empty_placeholder) AppCompatTextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initObjects();
        setUpRecyclerView();
    }

    private void initObjects() {
        AppRater.app_launched(this);
        ButterKnife.bind(this);
        controller = new ListController(ListActivity.this, this);
        controller.init();

        setTitle("All notes");
    }

    private void setUpRecyclerView() {
        noteList.setEmptyView(emptyView);
        noteList.setLayoutManager(new LinearLayoutManager(this));
        noteList.setHasFixedSize(true);
    }

    @Override
    public void setDataToAdapter(Cursor cursor) {
        adapter = new NotesCursorAdapter(cursor);
        noteList.setAdapter(adapter);
    }

    @Override
    public void startActivity(String noteUri) {
        Intent intent = new Intent(ListActivity.this, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_NOTE_URI, noteUri);
        startActivity(intent);
    }

    @Override
    public void resetLoader() {
        adapter.swapCursor(null);
    }

    @Override
    public void onDeleteSuccess() {

    }

    @Override
    public void onDeleteFailed() {

    }

    @Override
    public void onPasswordisInvalid() {
        Toast.makeText(ListActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.list_add_note)
    public void onAddNoteClick() {
        PickColorDialog pickColorDialog = new PickColorDialog(ListActivity.this);
        pickColorDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(ListActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public class NotesCursorAdapter extends CursorRecyclerviewAdapter<NotesCursorAdapter.ViewHolder> {

        NotesCursorAdapter(Cursor cursor) {
            super(ListActivity.this, cursor);
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
                        controller.showDeleteConfirmationDialog(note.get_id());
                    }

                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, note.get_id());
                        String uriString = String.valueOf(uri);
                        controller.onItemClick(uriString, note.getPassword());
                    }
                });
            }

        }
    }

}
