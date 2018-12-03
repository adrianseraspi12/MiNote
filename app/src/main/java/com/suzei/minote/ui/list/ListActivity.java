package com.suzei.minote.ui.list;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;

import com.suzei.minote.R;
import com.suzei.minote.data.DataSourceImpl;
import com.suzei.minote.ui.settings.SettingsActivity;

public class ListActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle(R.string.all_notes);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        ListFragment listFragment = ListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.list_container, listFragment)
                .commit();

        new ListPresenter(
                new DataSourceImpl(getApplicationContext()),
                listFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(ListActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
