package com.suzei.minote.ui.editor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.suzei.minote.Injection
import com.suzei.minote.R

class EditorActivity : AppCompatActivity() {

    companion object {

        val EXTRA_NOTE_ID = "EXTRA_NOTE_ID"

        val FRAGMENT_EDITOR_TAG = "EDITOR_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val itemId = intent.getIntExtra(EXTRA_NOTE_ID, -1)

        val fm = supportFragmentManager

        var editorFragment = fm.findFragmentByTag(FRAGMENT_EDITOR_TAG) as EditorFragment?

        if (editorFragment == null) {

            editorFragment = EditorFragment.newInstance()

            fm.beginTransaction()
                    .replace(R.id.editor_container, editorFragment!!, FRAGMENT_EDITOR_TAG)
                    .commit()

            if (itemId != -1) {
                EditorPresenter(itemId,
                        Injection.provideDataSourceImpl(applicationContext),
                        editorFragment)
            } else {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                EditorPresenter(prefs,
                        Injection.provideDataSourceImpl(applicationContext),
                        editorFragment)
            }

        }

    }

}