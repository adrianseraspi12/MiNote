package com.suzei.minote.utils.dialogs

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.card.MaterialCardView
import com.suzei.minote.R
import kotlinx.android.synthetic.main.fullscreen_dialog_password.*

open class PasswordDialog : DialogFragment(), View.OnClickListener {

    private var currentPassword = ""
    private var verifyPassword = ""
    private var isVerifying = false
    private var listener: ((String) -> Unit)? = null

    companion object {

        fun instance(password: String = "", listener: ((String) -> Unit)): PasswordDialog {
            val passwordDialog = PasswordDialog()
            passwordDialog.currentPassword = password
            passwordDialog.listener = listener
            if (password.isNotEmpty()) {
                passwordDialog.isVerifying = true
            }
            return passwordDialog
        }

    }

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
        setupNumberBtnClickListener()
        setupClearBtnClickListener()
        dialog_password_btn_cancel.setOnClickListener {
            listener?.invoke("")
            dismiss()
        }
    }

    override fun onClick(v: View?) {
        val container = v as MaterialCardView
        val childTextView = container.getChildAt(0) as TextView
        val secondaryColor = ResourcesCompat.getColor(resources, R.color.secondaryColor, null)
        val textNumber = childTextView.text.toString()

        val textCount: Int

        if (isVerifying) {
            textCount = verifyPassword.length
            verifyPassword += textNumber
        } else {
            textCount = currentPassword.length
            currentPassword += textNumber
        }

        setCodeColor(textCount, secondaryColor)

        if (textCount == 3) {
            if (isVerifying) {
                if (currentPassword == verifyPassword) {
                    listener?.invoke(currentPassword)
                    dismiss()
                    return
                }

                dialog_password_tv_title.setText(R.string.wrong_passcode)
                verifyPassword = ""
                resetCodeColor()
                return
            }

            Handler(Looper.getMainLooper()).postDelayed({
                isVerifying = true
                dialog_password_tv_title.setText(R.string.verify_passcode)
                resetCodeColor()
            }, 500)
        }
    }

    private fun setupNumberBtnClickListener() {
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
    }

    private fun setupClearBtnClickListener() {
        dialog_password_btn_clear.setOnClickListener {
            val currentPasswordCount: Int

            if (isVerifying) {
                if (verifyPassword.isEmpty()) return@setOnClickListener

                currentPasswordCount = verifyPassword.length
                verifyPassword = verifyPassword.substring(0, verifyPassword.length - 1)
            } else {
                if (currentPassword.isEmpty()) return@setOnClickListener

                currentPasswordCount = currentPassword.length
                currentPassword = currentPassword.substring(0, currentPassword.length - 1)
            }

            setCodeColor(currentPasswordCount - 1, Color.TRANSPARENT)
        }
    }

    private fun setCodeColor(position: Int, color: Int) {
        when (position) {

            0 -> {
                dialog_password_first_code.setCardBackgroundColor(color)
            }

            1 -> {
                dialog_password_second_code.setCardBackgroundColor(color)
            }

            2 -> {
                dialog_password_third_code.setCardBackgroundColor(color)
            }

            3 -> {
                dialog_password_fourth_code.setCardBackgroundColor(color)
            }

        }
    }

    private fun resetCodeColor() {
        dialog_password_first_code.setCardBackgroundColor(Color.TRANSPARENT)
        dialog_password_second_code.setCardBackgroundColor(Color.TRANSPARENT)
        dialog_password_third_code.setCardBackgroundColor(Color.TRANSPARENT)
        dialog_password_fourth_code.setCardBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDestroyView() {
        if (dialog != null) {
            listener = null
            dialog!!.setOnDismissListener(null)
        }
        super.onDestroyView()
    }
}