package com.suzei.minote.ui.list;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import com.suzei.minote.R;
import com.suzei.minote.data.DataSourceImpl;
import com.suzei.minote.preference.SettingsActivity;

public class ListActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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
