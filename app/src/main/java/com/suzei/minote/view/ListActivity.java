package com.suzei.minote.view;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.suzei.minote.R;
import com.suzei.minote.adapter.NotesCursorAdapter;
import com.suzei.minote.data.NoteContract.NoteEntry;
import com.suzei.minote.logic.Controller;
import com.suzei.minote.preference.SettingsActivity;
import com.suzei.minote.utils.RecyclerViewEmptySupport;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity extends AppCompatActivity implements NotesView {

    private NotesCursorAdapter adapter;

    @BindView(R.id.list_notes) RecyclerViewEmptySupport noteList;
    @BindView(R.id.list_empty_placeholder) LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initObjects();
        setUpRecyclerView();
    }

    private void initObjects() {
        ButterKnife.bind(this);
        setTitle(getString(R.string.all_notes));

        Controller controller = new Controller(
                ListActivity.this,
                NoteEntry.CONTENT_URI,
                this);
        controller.init();
    }

    private void setUpRecyclerView() {
        noteList.setEmptyView(emptyView);
        noteList.setLayoutManager(new LinearLayoutManager(this));
        noteList.setHasFixedSize(true);
    }

    @Override
    public void showDataToUi(Cursor cursor) {
        adapter = new NotesCursorAdapter(ListActivity.this, cursor);
        noteList.setAdapter(adapter);
    }

    @Override
    public void resetLoader() {
        adapter.swapCursor(null);
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

}
