package com.suzei.minote.ui.editor;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.suzei.minote.R;
import com.suzei.minote.data.DataSourceImpl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class EditorActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "EXTRA_NOTE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        int itemId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);

        EditorFragment editorFragment = EditorFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.editor_container, editorFragment)
                .commit();

        if (itemId != -1) {
            new EditorPresenter(itemId,
                    new DataSourceImpl(getApplicationContext()),
                    editorFragment);
        }
        else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            new EditorPresenter(prefs,
                    new DataSourceImpl(getApplicationContext()),
                    editorFragment);
        }

    }

}