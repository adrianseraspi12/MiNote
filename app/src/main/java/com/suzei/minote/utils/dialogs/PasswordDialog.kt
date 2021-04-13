package com.suzei.minote.utils.dialogs

import android.content.DialogInterface
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
import com.suzei.minote.databinding.FullscreenDialogPasswordBinding

open class PasswordDialog : DialogFragment(), View.OnClickListener {

    private var currentPassword = ""
    private var verifyPassword = ""
    private var isVerifying = false
    private var listener: ((String) -> Unit)? = null

    private var _binding: FullscreenDialogPasswordBinding? = null
    private val binding get() = _binding!!

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
                              savedInstanceState: Bundle?): View {
        _binding = FullscreenDialogPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNumberBtnClickListener()
        setupClearBtnClickListener()
        binding.dialogPasswordBtnCancel.setOnClickListener {
            listener?.invoke("")
            dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        listener?.invoke("")
        super.onCancel(dialog)
    }

    override fun onDestroyView() {
        _binding = null
        if (dialog != null) {
            listener = null
            dialog!!.setOnDismissListener(null)
        }
        super.onDestroyView()
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

                binding.dialogPasswordTvTitle.setText(R.string.wrong_passcode)
                verifyPassword = ""
                resetCodeColor()
                return
            }

            Handler(Looper.getMainLooper()).postDelayed({
                isVerifying = true
                binding.dialogPasswordTvTitle.setText(R.string.verify_passcode)
                resetCodeColor()
            }, 500)
        }
    }

    private fun setupNumberBtnClickListener() {
        binding.buttonOne.setOnClickListener(this)
        binding.buttonTwo.setOnClickListener(this)
        binding.buttonThree.setOnClickListener(this)
        binding.buttonFour.setOnClickListener(this)
        binding.buttonFive.setOnClickListener(this)
        binding.buttonSix.setOnClickListener(this)
        binding.buttonSeven.setOnClickListener(this)
        binding.buttonEight.setOnClickListener(this)
        binding.buttonNine.setOnClickListener(this)
        binding.buttonZero.setOnClickListener(this)
    }

    private fun setupClearBtnClickListener() {
        binding.dialogPasswordBtnClear.setOnClickListener {
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
                binding.dialogPasswordFirstCode.setCardBackgroundColor(color)
            }

            1 -> {
                binding.dialogPasswordSecondCode.setCardBackgroundColor(color)
            }

            2 -> {
                binding.dialogPasswordThirdCode.setCardBackgroundColor(color)
            }

            3 -> {
                binding.dialogPasswordFourthCode.setCardBackgroundColor(color)
            }

        }
    }

    private fun resetCodeColor() {
        binding.dialogPasswordFirstCode.setCardBackgroundColor(Color.TRANSPARENT)
        binding.dialogPasswordSecondCode.setCardBackgroundColor(Color.TRANSPARENT)
        binding.dialogPasswordThirdCode.setCardBackgroundColor(Color.TRANSPARENT)
        binding.dialogPasswordFourthCode.setCardBackgroundColor(Color.TRANSPARENT)
    }
}