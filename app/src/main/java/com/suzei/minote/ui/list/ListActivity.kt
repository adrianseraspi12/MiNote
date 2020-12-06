package com.suzei.minote.ui.list

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import com.suzei.minote.Injection
import com.suzei.minote.R
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.editor.todo.EditorTodoActivity
import com.suzei.minote.ui.list.notes.ListNoteFragment
import com.suzei.minote.ui.list.notes.ListNotePresenter
import com.suzei.minote.ui.list.todo.ListTodoFragment
import com.suzei.minote.ui.list.todo.ListTodoPresenter
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.dialogs.SelectNoteDialog
import com.suzei.minote.utils.dialogs.SelectNoteDialogListener
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.custom_bottom_navigation.*

class ListActivity : AppCompatActivity() {

    private lateinit var fm: FragmentManager
    private val listNoteFragment = ListNoteFragment.newInstance()
    private val listTodoFragment = ListTodoFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        LogMe.info("LOG ListActivity = onCreate()")

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        fm = supportFragmentManager
        setupCustomBottomNavigation()
        setupFabClick()
    }

    private fun setupCustomBottomNavigation() {
        val selectedColor = ResourcesCompat.getColor(resources, R.color.secondaryColor, null)
        val unselectedColor = ResourcesCompat.getColor(resources, R.color.unselectedColor, null)

        btn_nav_notes.iconTint = ColorStateList.valueOf(selectedColor)
        btn_nav_todo.iconTint = ColorStateList.valueOf(unselectedColor)
        showFragment(listNoteFragment)

        ListNotePresenter(
                Injection.provideDataSourceImpl(applicationContext),
                listNoteFragment)
        ListTodoPresenter(
                Injection.provideTodoRepository(applicationContext),
                listTodoFragment)

        btn_nav_notes.setOnClickListener {
            btn_nav_notes.iconTint = ColorStateList.valueOf(selectedColor)
            btn_nav_todo.iconTint = ColorStateList.valueOf(unselectedColor)
            showFragment(listNoteFragment)
        }

        btn_nav_todo.setOnClickListener {
            btn_nav_notes.iconTint = ColorStateList.valueOf(unselectedColor)
            btn_nav_todo.iconTint = ColorStateList.valueOf(selectedColor)
            showFragment(listTodoFragment)
        }
    }

    private fun setupFabClick() {
        list_fab.setOnClickListener {
            val dialog = SelectNoteDialog()
            dialog.setOnCreateClickListener(object : SelectNoteDialogListener {

                override fun onCreateClick(type: Int) {

                    when (type) {

                        SelectNoteDialog.NOTE_PAD ->
                            startActivity(Intent(
                                    this@ListActivity,
                                    EditorNoteActivity::class.java))

                        SelectNoteDialog.TODO_LIST ->
                            startActivity(Intent(
                                    this@ListActivity,
                                    EditorTodoActivity::class.java))

                    }

                }

            })

            dialog.show(fm, null)
        }
    }

    private fun showFragment(fragment: Fragment) {
        fm.beginTransaction().replace(list_container.id, fragment).commit()
    }
}
