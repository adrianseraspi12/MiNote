package com.suzei.minote.utils.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.suzei.minote.R

import kotlinx.android.synthetic.main.dialog_create_note.*

class SelectNoteDialog: DialogFragment(), View.OnClickListener {

    companion object {

        val NOTE_PAD = 0
        val TODO_LIST = 1

    }

    var selectNoteDialogListener: SelectNoteDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.InputDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_create_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog_create_note_button.setOnClickListener(this)
        dialog_create_note_cancel.setOnClickListener(this)
    }

    fun setOnCreateClickListener(listener: SelectNoteDialogListener) {
        this.selectNoteDialogListener = listener
    }

    override fun dismiss() {
        selectNoteDialogListener = null
        super.dismiss()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.dialog_create_note_button -> {
                val selectedId = dialog_create_note_types.checkedRadioButtonId

                when(selectedId) {

                    R.id.dialog_create_note_pad ->
                        selectNoteDialogListener?.onCreateClick(NOTE_PAD)

                    R.id.dialog_create_note_todo_list ->
                        selectNoteDialogListener?.onCreateClick(TODO_LIST)

                }

                dismiss()

            }

            R.id.dialog_create_note_cancel -> dismiss()

        }
    }

}

interface SelectNoteDialogListener {

    fun onCreateClick(type: Int)

}