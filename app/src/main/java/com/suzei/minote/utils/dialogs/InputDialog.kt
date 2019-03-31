package com.suzei.minote.utils.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.suzei.minote.R
import kotlinx.android.synthetic.main.dialog_todo.*
import java.lang.IllegalArgumentException

class InputDialog: DialogFragment(), View.OnClickListener {

    companion object {

        val instance: InputDialog
            get() = InputDialog()

    }

    var inputDialogListener: InputDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setStyle(DialogFragment.STYLE_NORMAL, R.style.InputDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog_todo_add.setOnClickListener(this)
        dialog_todo_cancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val button = v as Button
        val id = button.id

        when (id) {

            R.id.dialog_todo_add -> {
                val message = dialog_todo_message.text.toString()
                inputDialogListener?.onAddClick(message)
                dismiss()
            }

            R.id.dialog_todo_cancel -> dismiss()

            else -> throw IllegalArgumentException("Invalid Id = $id")

        }
    }

    fun setOnAddClickListener(inputDialogListener: InputDialogListener) {
        this.inputDialogListener = inputDialogListener
    }

    override fun dismiss() {
        inputDialogListener = null
        super.dismiss()
    }

}

interface InputDialogListener {

    fun onAddClick(message: String?)

}