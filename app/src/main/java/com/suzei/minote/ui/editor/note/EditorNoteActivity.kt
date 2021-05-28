package com.suzei.minote.ui.editor.note

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suzei.minote.Injection
import com.suzei.minote.R

class EditorNoteActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_NOTE_ID = "EXTRA_NOTE_ID"

        const val FRAGMENT_EDITOR_NOTE_TAG = "EDITOR_NOTE_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity)

        val itemId = intent.getStringExtra(EXTRA_NOTE_ID)
        val fm = supportFragmentManager
        var editorFragment = fm.findFragmentByTag(FRAGMENT_EDITOR_NOTE_TAG) as EditorNoteFragment?

        if (editorFragment == null) {

            editorFragment = EditorNoteFragment.newInstance()

            fm.beginTransaction()
                    .replace(R.id.base_container, editorFragment, FRAGMENT_EDITOR_NOTE_TAG)
                    .commit()

            if (itemId != null) {
                EditorNotePresenter(itemId,
                        Injection.provideSharedPreference(this),
                        Injection.provideNoteDataSource(applicationContext),
                        editorFragment)
            } else {
                EditorNotePresenter(Injection.provideNoteDataSource(applicationContext),
                        Injection.provideSharedPreference(this),
                        editorFragment)
            }
        }
    }
}