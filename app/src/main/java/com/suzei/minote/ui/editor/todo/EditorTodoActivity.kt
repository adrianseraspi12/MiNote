package com.suzei.minote.ui.editor.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suzei.minote.Injection
import com.suzei.minote.R

class EditorTodoActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_TODO_ID = "EXTRA_TODO_ID"

        const val FRAGMENT_EDITOR_TODO_TAG = "EDITOR_TODO_FRAGMENT"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_todo)

        val itemId = intent.getStringExtra(EXTRA_TODO_ID)

        val fm = supportFragmentManager

        var editorFragment = fm.findFragmentByTag(FRAGMENT_EDITOR_TODO_TAG) as EditorTodoFragment?

        if (editorFragment == null) {
            editorFragment = EditorTodoFragment.newInstance()

            fm.beginTransaction()
                    .replace(R.id.editor_todo_container, editorFragment, FRAGMENT_EDITOR_TODO_TAG)
                    .commit()

            if (itemId != null) {
                EditorTodoPresenter(itemId,
                        Injection.provideTodoRepository(applicationContext),
                        editorFragment)
            } else {
                EditorTodoPresenter(Injection.provideTodoRepository(applicationContext),
                        editorFragment)
            }

        }

    }

}
