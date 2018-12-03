package com.suzei.minote.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.suzei.minote.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsFragment settingsFragment = SettingsFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        new SettingsPresenter(sharedPrefs, settingsFragment);

        setTitle("Settings");
    }
}
