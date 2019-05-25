package com.suzei.minote.utils.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.suzei.minote.R
import com.suzei.minote.ext.moveFocus
import com.suzei.minote.utils.LogMe
import kotlinx.android.synthetic.main.dialog_todo.*
import java.lang.IllegalArgumentException

class InputDialog: DialogFragment(), View.OnClickListener {

    companion object {

        fun instance(title: String,
                     actionTitle: String,
                     message: String): InputDialog {

            val dialog = InputDialog()
            dialog.setValues(title, actionTitle, message)

            return dialog
        }

    }

    var inputDialogListener: InputDialogListener? = null

    private lateinit var message: String
    private lateinit var actionTitle: String
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setStyle(STYLE_NORMAL, R.style.InputDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog_todo_add.setOnClickListener(this)
        dialog_todo_cancel.setOnClickListener(this)
        dialog_todo_message.setText(message)
        dialog_todo_add.text = actionTitle
        dialog_todo_title.text = title
        dialog_todo_message.moveFocus()
    }

    override fun onClick(v: View?) {
        val button = v as Button

        when (val id = button.id) {

            R.id.dialog_todo_add -> {
                val message = dialog_todo_message.text.toString()
                inputDialogListener?.onAddClick(message)
                dismiss()
            }

            R.id.dialog_todo_cancel -> dismiss()

            else -> throw IllegalArgumentException("Invalid Id = $id")

        }
    }

    private fun setValues(title: String, actionTitle: String, message: String) {
        this.title = title
        this.actionTitle = actionTitle
        this.message = message
    }

    fun setOnAddClickListener(inputDialogListener: InputDialogListener) {
        this.inputDialogListener = inputDialogListener
    }

    fun removeOnAddClickListener() {
        this.inputDialogListener = null
    }

    override fun dismiss() {
        removeOnAddClickListener()
        super.dismiss()
    }

}

interface InputDialogListener {

    fun onAddClick(message: String?)

}