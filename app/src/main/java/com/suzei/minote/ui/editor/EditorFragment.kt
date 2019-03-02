package com.suzei.minote.ui.editor


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.utils.ColorWheel
import com.suzei.minote.utils.KeyboardUtils
import com.suzei.minote.utils.Turing
import com.suzei.minote.utils.dialogs.BottomSheetFragment
import com.suzei.minote.utils.dialogs.PasswordDialog
import kotlinx.android.synthetic.main.fragment_editor.*

class EditorFragment : Fragment(), EditorContract.View {

    private lateinit var presenter: EditorContract.Presenter

    private var mPassword: String? = null
    private var noteColor = -1
    private var textColor = -1


    companion object {

        private val EXTRA_PASSWORD = "EXTRA_PASSWORD"
        private val EXTRA_NOTE_COLOR = "EXTRA_NOTE_COLOR"
        private val EXTRA_TEXT_COLOR = "EXTRA_TEXT_COLOR"

        internal fun newInstance(): EditorFragment {
            return EditorFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_editor, container, false)

        if (savedInstanceState != null) {
            noteColor = savedInstanceState.getInt(EXTRA_NOTE_COLOR, -1)
            textColor = savedInstanceState.getInt(EXTRA_TEXT_COLOR, -1)
            mPassword = savedInstanceState.getString(EXTRA_NOTE_COLOR, null)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editor_back_arrow.setOnClickListener {
            activity!!.finish()
        }

        editor_text_layout.setOnClickListener {
            editor_text.requestFocus()
            KeyboardUtils.showKeyboard(context!!, editor_text)
        }

        editor_menu.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.retainInstance = true
            bottomSheetFragment.setClickListener(object : BottomSheetFragment.ClickListener {

                override fun onEditPasswordClick() {
                    presenter.passwordDialog()
                    bottomSheetFragment.dismiss()
                }

                override fun onChangeNoteColorClick() {
                    val noteColor = (editor_root.background as ColorDrawable).color
                    presenter.noteColorWheel(noteColor)
                    bottomSheetFragment.dismiss()
                }

                override fun onChangeTextColorClick() {
                    val textColor = editor_text.currentTextColor
                    presenter.textColorWheel(textColor)
                    bottomSheetFragment.dismiss()
                }
            })

            bottomSheetFragment.show(fragmentManager!!, bottomSheetFragment.tag)
        }

        editor_save.setOnClickListener {
            val noteColor = (editor_root.background as ColorDrawable).color
            val hexNoteColor = String.format("#%06X", 0xFFFFFF and noteColor)

            val textColor = editor_text.currentTextColor
            val hexTextColor = String.format("#%06X", 0xFFFFFF and textColor)

            val title = editor_title.text.toString()
            val message = editor_text.text.toString()
            val password = mPassword?.let { it1 -> Turing.encrypt(it1) }

            presenter.saveNote(title, message, hexNoteColor, hexTextColor, password)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onResume() {
        super.onResume()
        if (noteColor != -1 || textColor != -1) {
            noteColor(noteColor)
            textColor(textColor)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val noteColor = (editor_root.background as ColorDrawable).color
        val textColor = editor_text.currentTextColor
        outState.putString(EXTRA_PASSWORD, mPassword)
        outState.putInt(EXTRA_NOTE_COLOR, noteColor)
        outState.putInt(EXTRA_TEXT_COLOR, textColor)
    }

    override fun setPresenter(presenter: EditorContract.Presenter) {
        this.presenter = presenter
    }

    override fun showNoteDetails(note: Notes) {
        editor_title.setText(note.title)
        editor_text.setText(note.message)
        noteColor(Color.parseColor(note.color))
        textColor(Color.parseColor(note.textColor))
    }

    override fun noteColor(noteColor: Int) {
        editor_root.setBackgroundColor(noteColor)
    }

    override fun textColor(textColor: Int) {
        editor_title.setTextColor(textColor)
        editor_text.setTextColor(textColor)
        editor_back_arrow.setColorFilter(textColor)
        editor_save.setColorFilter(textColor)
        editor_menu.setColorFilter(textColor)
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showColorWheel(title: String, initialColor: Int, colorWheel: ColorWheel) {
        ColorPickerDialogBuilder.with(context!!)
                .setTitle(title)
                .initialColor(initialColor)
                .density(6)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .setPositiveButton("Choose") {
                    dialogInterface,
                    i,
                    integers ->
                    colorWheel.onPositiveClick(i)
                }
                .setNegativeButton("Cancel") {
                    dialog,
                    which ->
                    dialog.dismiss()
                }
                .build()
                .show()
    }

    override fun showPasswordDialog() {
        val passwordDialog = PasswordDialog.instance
        passwordDialog.setOnClosePasswordDialog(object: PasswordDialog.PasswordDialogListener {

            override fun onClose(password: String) {
                mPassword = password
            }

        })
        passwordDialog.show(fragmentManager!!, "Password Dialog")
    }

}