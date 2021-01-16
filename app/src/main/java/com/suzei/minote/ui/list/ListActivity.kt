package com.suzei.minote.ui.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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
import com.suzei.minote.utils.dialogs.SelectNoteDialog
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.custom_bottom_navigation.*
import kotlinx.android.synthetic.main.toast_undo_delete.*

class ListActivity : AppCompatActivity() {

    private lateinit var fm: FragmentManager
    private lateinit var selectNoteDialog: SelectNoteDialog
    private val listNoteFragment = ListNoteFragment.newInstance()
    private val listTodoFragment = ListTodoFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        fm = supportFragmentManager
        setupSelectNoteDialog()
        setupCustomBottomNavigation()
        setupFabClick()
    }

    fun showToastUndo(message: String, onUndoClick: (() -> Unit)) {
        toast_undo_delete_root.alpha = 0f
        toast_undo_delete_root.visibility = View.VISIBLE
        toast_undo_delete_root
                .animate()
                .alpha(1f)
                .duration = 300
        toast_undo_delete_tv_title.text = message
        toast_delete_btn_undo.setOnClickListener {
            onUndoClick.invoke()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            toast_undo_delete_root
                    .animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            toast_undo_delete_root.visibility = View.GONE
                            toast_undo_delete_root.animate().setListener(null)
                        }
                    })
        }, 4000)
    }

    private fun setupSelectNoteDialog() {
        selectNoteDialog = SelectNoteDialog.newInstance {
            selectNoteDialog.dismiss()
            when (it) {
                SelectNoteDialog.NOTE_PAD ->
                    this.startActivity(Intent(
                            this@ListActivity,
                            EditorNoteActivity::class.java))

                SelectNoteDialog.TODO_LIST ->
                    this.startActivity(Intent(
                            this@ListActivity,
                            EditorTodoActivity::class.java))
            }
        }
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
            fm.let {
                selectNoteDialog.show(it, SelectNoteDialog.TAG)
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        fm.beginTransaction().replace(list_container.id, fragment).commit()
    }
}
