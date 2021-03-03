package com.suzei.minote.utils.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.suzei.minote.R
import kotlinx.android.synthetic.main.dialog_create_note.*

class SelectNoteDialog : BottomSheetDialogFragment() {

    companion object {

        const val NOTE_PAD = 0
        const val TODO_LIST = 1
        const val TAG = "SELECT_NOTE_DIALOG"

        fun newInstance(selectNoteDialogListener: ((Int) -> Unit)?): SelectNoteDialog {
            val fragment = SelectNoteDialog()
            fragment.selectNoteDialogListener = selectNoteDialogListener
            return fragment
        }

    }

    private var selectNoteDialogListener: ((Int) -> Unit)? = null
    private var currentSelectedCatagory = NOTE_PAD

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_create_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val primaryColor = ResourcesCompat.getColor(resources, R.color.primaryColor, null)
        val secondaryColor = ResourcesCompat.getColor(resources, R.color.secondaryColor, null)

        create_note_container_note.setOnClickListener {
            changeCategoryColor(secondaryColor, primaryColor)
            currentSelectedCatagory = NOTE_PAD
        }

        create_note_container_todo.setOnClickListener {
            changeCategoryColor(primaryColor, secondaryColor)
            currentSelectedCatagory = TODO_LIST
        }

        create_note_btn_create.setOnClickListener {
            selectNoteDialogListener?.invoke(currentSelectedCatagory)
        }
    }

    private fun changeCategoryColor(noteColor: Int, todoColor: Int) {
        create_note_container_note.strokeColor = noteColor
        create_note_container_todo.strokeColor = todoColor
        create_note_container_note.invalidate()
        create_note_container_todo.invalidate()
    }

    override fun dismiss() {
        currentSelectedCatagory = NOTE_PAD
        super.dismiss()
    }
}