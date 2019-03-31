package com.suzei.minote.ui.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import com.suzei.minote.Injection
import com.suzei.minote.R
import com.suzei.minote.ui.settings.SettingsActivity

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.editor.todo.EditorTodoActivity
import com.suzei.minote.ui.list.notes.ListNoteFragment
import com.suzei.minote.ui.list.notes.ListNotePresenter
import com.suzei.minote.ui.list.todo.ListTodoFragment

import kotlinx.android.synthetic.main.activity_list.*
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter
import uk.co.markormesher.android_fab.SpeedDialMenuItem
import java.lang.IllegalArgumentException

class ListActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {

        private const val LIST_NOTE_FRAGMENT_TAG = "LIST_NOTE_FRAGMENT"

        private const val LIST_TODO_FRAGMENT_TAG = "LIST_TODO_FRAGMENT"
    }

    private lateinit var fm: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setTitle(R.string.all_notes)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        fm = supportFragmentManager

        list_fab.speedDialMenuAdapter = speedDialAdapter

        selectFragment(bottomNavigationView.menu.getItem(0))
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_settings) {
            startActivity(Intent(
                    this@ListActivity,
                    SettingsActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        selectFragment(item)
        return true
    }

    private fun selectFragment(item: MenuItem) {

        item.isChecked = true

        when (item.itemId) {

            R.id.nav_note -> {
                showListOfNote()
            }

            R.id.nav_todo -> {
                showListOfTodo()
            }

            else -> {
                throw IllegalArgumentException("Invalid Menu Id = ${item.itemId}")
            }

        }
    }

    private fun showListOfTodo() {
        var listTodoFragment = fm.findFragmentByTag(LIST_TODO_FRAGMENT_TAG) as ListTodoFragment?

        if (listTodoFragment == null) {
            //  instance of listtodo fragment
            //  showFragment
            //  initialize presenter
        }

    }

    private fun showListOfNote() {
        var listNoteFragment = fm.findFragmentByTag(LIST_NOTE_FRAGMENT_TAG) as ListNoteFragment?

        if (listNoteFragment == null) {
            listNoteFragment = ListNoteFragment.newInstance()
            showFragment(listNoteFragment, LIST_NOTE_FRAGMENT_TAG)

            ListNotePresenter(
                    Injection.provideDataSourceImpl(applicationContext),
                    listNoteFragment)
        }
    }

    private fun showFragment(fragment: Fragment, fragmentTag: String) {
        fm.beginTransaction()
                .replace(R.id.list_container,
                         fragment, fragmentTag)
                .commit()
    }

    private val speedDialAdapter = object: SpeedDialMenuAdapter() {

        override fun getCount(): Int {
            return 2
        }

        override fun getMenuItem(context: Context, position: Int): SpeedDialMenuItem =
                when(position) {

                    0 -> SpeedDialMenuItem(context, R.drawable.lock_close, "Create Note")
                    1 -> SpeedDialMenuItem(context, R.drawable.settings, "Create Todo")

                    else -> throw IllegalArgumentException("Invalid position = $position")
                }

        override fun onMenuItemClick(position: Int): Boolean {
            return when(position) {

                0 -> {
                    startActivity(Intent(
                            this@ListActivity,
                            EditorNoteActivity::class.java))
                    true
                }

                1 -> {
                    startActivity(Intent(
                            this@ListActivity,
                            EditorTodoActivity::class.java))
                    true
                }


                else -> throw IllegalArgumentException("Invalid position = $position")
            }
        }

    }


}
