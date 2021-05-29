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
import com.suzei.minote.Injection
import com.suzei.minote.R
import com.suzei.minote.databinding.ActivityListBinding
import com.suzei.minote.databinding.CustomBottomNavigationBinding
import com.suzei.minote.databinding.ToastUndoDeleteBinding
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.editor.todo.EditorTodoActivity
import com.suzei.minote.ui.list.notes.ListNoteFragment
import com.suzei.minote.ui.list.notes.ListNotePresenter
import com.suzei.minote.ui.list.todo.ListTodoFragment
import com.suzei.minote.ui.list.todo.ListTodoPresenter
import com.suzei.minote.utils.InAppReview
import com.suzei.minote.utils.OnOneOffClickListener
import com.suzei.minote.utils.dialogs.SelectNoteDialog

class ListActivity : AppCompatActivity() {

    private lateinit var fm: FragmentManager
    private lateinit var selectNoteDialog: SelectNoteDialog
    private lateinit var activityListBinding: ActivityListBinding
    private lateinit var toastUndoBinding: ToastUndoDeleteBinding
    private lateinit var bottomNavigationBinding: CustomBottomNavigationBinding
    private val listNoteFragment = ListNoteFragment.newInstance()
    private val listTodoFragment = ListTodoFragment.newInstance()
    private var callback: ToastCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityListBinding = ActivityListBinding.inflate(layoutInflater)
        toastUndoBinding = activityListBinding.listToastUndo
        bottomNavigationBinding = activityListBinding.listBottomNavigation
        setContentView(activityListBinding.root)
        fm = supportFragmentManager
        initSelectNoteDialog()
        initPresenters()
        setupFragmentTransaction()
        setupCustomBottomNavigation()
        setupFabClick()
        InAppReview.run(this)
    }

    override fun onPause() {
        super.onPause()
        if (toastUndoBinding.toastUndoDeleteRoot.visibility == View.VISIBLE) {
            hideToast()
        }
    }

    fun showToastUndo(message: String, callback: ToastCallback) {
        this.callback = callback
        if (toastUndoBinding.toastUndoDeleteRoot.visibility == View.VISIBLE) {
            toastUndoBinding.toastUndoDeleteRoot.visibility = View.GONE
            toastUndoBinding.toastUndoDeleteRoot.animate().setListener(null)
        }
        toastUndoBinding.toastUndoDeleteRoot.alpha = 0f
        toastUndoBinding.toastUndoDeleteRoot.visibility = View.VISIBLE
        toastUndoBinding.toastUndoDeleteRoot
                .animate()
                .alpha(1f)
                .duration = 300
        toastUndoBinding.toastUndoDeleteTvTitle.text = message
        toastUndoBinding.toastDeleteBtnUndo.setOnClickListener {
            callback.onUndoClick()
            toastUndoBinding.toastUndoDeleteRoot.visibility = View.GONE
            toastUndoBinding.toastUndoDeleteRoot.alpha = 0f
            toastUndoBinding.toastUndoDeleteRoot.animate().setListener(null)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (toastUndoBinding.toastUndoDeleteRoot.visibility == View.VISIBLE) {
                toastUndoBinding.toastUndoDeleteRoot
                        .animate()
                        .alpha(0f)
                        .setDuration(300)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                hideToast()
                            }
                        })
            }
        }, 4000)
    }

    private fun hideToast() {
        toastUndoBinding.toastUndoDeleteRoot.visibility = View.GONE
        toastUndoBinding.toastUndoDeleteRoot.animate().setListener(null)
        callback?.onToastDismiss()
        callback = null
    }

    private fun setFragment(visibleFragment: Fragment, invisibleFragmnet: Fragment) {
        fm.beginTransaction().apply {
            show(visibleFragment)
            hide(invisibleFragmnet)
            commit()
        }
    }

    private fun setupFragmentTransaction() {
        fm.beginTransaction().apply {
            add(activityListBinding.listContainer.id, listNoteFragment, ListNoteFragment.TAG)
            add(activityListBinding.listContainer.id, listTodoFragment, ListTodoFragment.TAG)
            show(listNoteFragment)
            hide(listTodoFragment)
            commit()
        }
    }

    private fun initPresenters() {
        ListNotePresenter(
                Injection.provideNoteDataSource(applicationContext),
                listNoteFragment)
        ListTodoPresenter(
                Injection.provideTodoDataSource(applicationContext),
                listTodoFragment)
    }

    private fun initSelectNoteDialog() {
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

    private fun setupFabClick() {
        activityListBinding.listFab.setOnClickListener(object : OnOneOffClickListener() {
            override fun onSingleClick(view: View?) {
                fm.let {
                    selectNoteDialog.show(it, SelectNoteDialog.TAG)
                }
            }
        })
    }

    private fun setupCustomBottomNavigation() {
        val selectedColor = ResourcesCompat.getColor(resources, R.color.secondaryColor, null)
        val unselectedColor = ResourcesCompat.getColor(resources, R.color.unselectedColor, null)

        bottomNavigationBinding.btnNavNotes.iconTint = ColorStateList.valueOf(selectedColor)
        bottomNavigationBinding.btnNavTodo.iconTint = ColorStateList.valueOf(unselectedColor)

        bottomNavigationBinding.btnNavNotes.setOnClickListener {
            bottomNavigationBinding.btnNavNotes.iconTint = ColorStateList.valueOf(selectedColor)
            bottomNavigationBinding.btnNavTodo.iconTint = ColorStateList.valueOf(unselectedColor)
            setFragment(listNoteFragment, listTodoFragment)
        }

        bottomNavigationBinding.btnNavTodo.setOnClickListener {
            bottomNavigationBinding.btnNavNotes.iconTint = ColorStateList.valueOf(unselectedColor)
            bottomNavigationBinding.btnNavTodo.iconTint = ColorStateList.valueOf(selectedColor)
            setFragment(listTodoFragment, listNoteFragment)
        }
    }
}
