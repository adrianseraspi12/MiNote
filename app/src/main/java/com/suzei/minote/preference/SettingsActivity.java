package com.suzei.minote.preference;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.suzei.minote.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        setTitle(getString(R.string.about));
    }
}
