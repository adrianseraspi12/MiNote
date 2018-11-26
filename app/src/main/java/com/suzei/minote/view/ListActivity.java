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
import com.suzei.minote.data.DataSourceImpl;
import com.suzei.minote.data.NoteContract.NoteEntry;
import com.suzei.minote.data.Notes;
import com.suzei.minote.logic.Controller;
import com.suzei.minote.preference.SettingsActivity;
import com.suzei.minote.utils.RecyclerViewEmptySupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListFragment listFragment = ListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.list_container, listFragment)
                .commit();

        new Presenter(
                new DataSourceImpl(getApplicationContext()),
                listFragment);
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
