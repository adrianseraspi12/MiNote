package com.suzei.minote.ui.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.preference.PreferenceManager
import com.suzei.minote.Injection
import com.suzei.minote.R
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.editor.todo.EditorTodoActivity
import com.suzei.minote.ui.list.notes.ListNoteFragment
import com.suzei.minote.ui.list.notes.ListNotePresenter
import com.suzei.minote.ui.list.todo.ListTodoFragment
import com.suzei.minote.ui.list.todo.ListTodoPresenter
import com.suzei.minote.ui.settings.SettingsActivity
import com.suzei.minote.utils.LogMe
import kotlinx.android.synthetic.main.activity_list.*
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter
import uk.co.markormesher.android_fab.SpeedDialMenuItem

class ListActivity : AppCompatActivity() {

    private lateinit var fm: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        setSupportActionBar(list_toolbar)

        LogMe.info("Instance = New Activity")
        fm = supportFragmentManager

        list_view_pager.adapter = ListTabPagerAdapter()
        list_tab_layout.setupWithViewPager(list_view_pager)

        list_fab.speedDialMenuAdapter = speedDialAdapter
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

    inner class ListTabPagerAdapter: FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment {
            when (position) {

                0 -> {
                    val listNoteFragment = ListNoteFragment.newInstance()

                    ListNotePresenter(
                            Injection.provideDataSourceImpl(applicationContext),
                            listNoteFragment)

                    return listNoteFragment
                }

                1 -> {
                    val listTodoFragment = ListTodoFragment.newInstance()

                    ListTodoPresenter(
                            Injection.provideTodoRepository(applicationContext),
                            listTodoFragment)

                    return listTodoFragment
                }

            }

            throw IllegalArgumentException("Invalid Position $position")
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {

                0 -> return "Notes"

                1 -> return "Todo"

            }

            return super.getPageTitle(position)
        }

        override fun getCount(): Int {
            return 2
        }

    }

}
