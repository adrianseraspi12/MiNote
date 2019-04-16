package com.suzei.minote.ui.editor.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.suzei.minote.Injection
import com.suzei.minote.R

class EditorTodoActivity : AppCompatActivity() {

    companion object {

        val FRAGMENT_EDITOR_NOTE_TAG = "EDITOR_NOTE_FRAGMENT"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_todo)

        val fm = supportFragmentManager

        var editorFragment = fm.findFragmentByTag(FRAGMENT_EDITOR_NOTE_TAG) as EditorTodoFragment?

        if (editorFragment == null) {
            editorFragment = EditorTodoFragment.newInstance()

            fm.beginTransaction()
                    .replace(R.id.editor_todo_container, editorFragment, FRAGMENT_EDITOR_NOTE_TAG)
                    .commit()

            EditorTodoPresenter(
                    Injection.provideTodoRepository(applicationContext),
                    editorFragment)

        }
    }
}
