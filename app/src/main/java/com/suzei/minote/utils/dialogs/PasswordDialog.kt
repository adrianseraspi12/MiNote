package com.suzei.minote.utils.dialogs

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.suzei.minote.R
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fullscreen_dialog_password.*

open class PasswordDialog : DialogFragment(), View.OnClickListener {

    private var listener: PasswordDialogListener? = null

    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.PasswordDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fullscreen_dialog_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonOne.setOnClickListener(this)
        buttonTwo.setOnClickListener(this)
        buttonThree.setOnClickListener(this)
        buttonFour.setOnClickListener(this)
        buttonFive.setOnClickListener(this)
        buttonSix.setOnClickListener(this)
        buttonSeven.setOnClickListener(this)
        buttonEight.setOnClickListener(this)
        buttonNine.setOnClickListener(this)
        buttonZero.setOnClickListener(this)
        buttonClear.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val button = v as Button
        val textNumber = button.text.toString()

        if (textNumber == "CLEAR" && !TextUtils.isEmpty(password)) {
            password = password.substring(0, password.length -1)
            password_dots.setText(password)
            return
        }

        password_dots.append(textNumber)
        password += textNumber

        if (password.length == PASSWORD_LENGTH) {
            listener?.onClose(password)
            dismiss()
        }
    }

    fun setOnClosePasswordDialog(listener: PasswordDialogListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        if (dialog != null) {
            dialog!!.setOnDismissListener(null)
        }
        super.onDestroyView()
    }

    interface PasswordDialogListener {

        fun onClose(password: String)

    }

    companion object {

        private val PASSWORD_LENGTH = 4

        val instance: PasswordDialog
            get() = PasswordDialog()
    }
}