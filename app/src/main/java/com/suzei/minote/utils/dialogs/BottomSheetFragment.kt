package com.suzei.minote.utils.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.suzei.minote.R
import kotlinx.android.synthetic.main.bottom_sheet_dialog.*

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var listener: ClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bsd_change_note_color.setOnClickListener(buttonClickListener)
        bsd_change_text_color.setOnClickListener(buttonClickListener)
        bsd_edit_password.setOnClickListener(buttonClickListener)
    }

    private val buttonClickListener: View.OnClickListener = View.OnClickListener { v ->
        when(v?.id ) {

            R.id.bsd_change_note_color -> {
                if (listener != null) {
                    listener?.onChangeNoteColorClick()
                }
            }

            R.id.bsd_change_text_color -> {
                if (listener != null) {
                    listener?.onChangeTextColorClick()
                }
            }

            R.id.bsd_edit_password -> {
                if (listener != null) {
                    listener?.onEditPasswordClick()
                }
            }

        }
    }

    fun setClickListener(listener: ClickListener) {
        this.listener = listener
    }

    fun destroy() {
        listener = null
    }

    interface ClickListener {

        fun onEditPasswordClick()

        fun onChangeNoteColorClick()

        fun onChangeTextColorClick()

    }

}