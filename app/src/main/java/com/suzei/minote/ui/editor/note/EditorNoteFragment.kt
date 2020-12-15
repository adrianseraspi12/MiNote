package com.suzei.minote.ui.editor.note


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.ext.moveFocus
import com.suzei.minote.utils.ColorWheel
import com.suzei.minote.utils.Turing
import com.suzei.minote.utils.dialogs.PasswordDialog
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapter
import com.suzei.minote.utils.recycler_view.decorator.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.bottomsheet_edit_note.*
import kotlinx.android.synthetic.main.fragment_editor.*

class EditorNoteFragment : Fragment(), EditorNoteContract.View {

    private lateinit var presenter: EditorNoteContract.Presenter

    private var mPassword: String? = null
    private var noteColor = -1
    private var textColor = -1

    private lateinit var itemDecoration: GridSpacingItemDecoration

    companion object {

        private const val EXTRA_PASSWORD = "EXTRA_PASSWORD"
        private const val EXTRA_NOTE_COLOR = "EXTRA_NOTE_COLOR"
        private const val EXTRA_TEXT_COLOR = "EXTRA_TEXT_COLOR"

        internal fun newInstance(): EditorNoteFragment {
            return EditorNoteFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        itemDecoration = GridSpacingItemDecoration(
                6,
                resources.getDimensionPixelSize(R.dimen.colorListSpacing),
                false
        )
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
//        Change Note Color
//        val noteColor = (editor_root.background as ColorDrawable).color
//        presenter.noteColorWheel(noteColor)

//        Change Text Color
//        val textColor = editor_text.currentTextColor
//        presenter.textColorWheel(textColor)

        setupBottomSheet()
        setupSaveOnClick()
        setupBack()
        setupLock()
        setupNoteColorRecyclerView()
        setupTextColorRecyclerView()
    }

    private fun setupLock() {
        bottom_sheet_switch_lock.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) presenter.passwordDialog()
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

    override fun setPresenter(presenter: EditorNoteContract.Presenter) {
        this.presenter = presenter
    }

    override fun showNoteDetails(note: Notes) {
        editor_title.setText(note.title)
        editor_text.setText(note.message)
        noteColor(Color.parseColor(note.color))
        textColor(Color.parseColor(note.textColor))

        editor_title.moveFocus()
    }

    override fun noteColor(noteColor: Int) {
        editor_root.setBackgroundColor(noteColor)
    }

    override fun textColor(textColor: Int) {
        editor_title.setTextColor(textColor)
        editor_text.setTextColor(textColor)
        editor_back_arrow.setColorFilter(textColor)
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
                .setPositiveButton("Choose") { dialogInterface,
                                               i,
                                               integers ->
                    colorWheel.onPositiveClick(i)
                }
                .setNegativeButton("Cancel") { dialog,
                                               which ->
                    dialog.dismiss()
                }
                .build()
                .show()
    }

    override fun showPasswordDialog() {
        val passwordDialog = PasswordDialog.instance
        passwordDialog.setOnClosePasswordDialog(object : PasswordDialog.PasswordDialogListener {

            override fun onClose(password: String) {
                mPassword = password
            }

        })
        passwordDialog.show(fragmentManager!!, "Password Dialog")
    }

    private fun setupBack() {
        editor_back_arrow.setOnClickListener {
            activity!!.finish()
        }
    }

    private fun setupSaveOnClick() {
        editor_btn_save.setOnClickListener {
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

    private fun setupBottomSheet() {
        //  Setup Bottomsheet Behavior
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet_settings_container)
        bottomsheet_settings_container.viewTreeObserver.addOnGlobalLayoutListener {
            val hiddenView = bottomsheet_settings_container.getChildAt(2)
            bottomSheetBehavior.peekHeight = hiddenView.top
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupNoteColorRecyclerView() {
        val colors = listOf(
                Color.parseColor("#FF6464"),
                Color.parseColor("#FDEC61"),
                Color.parseColor("#76A0FF"),
                Color.parseColor("#96F07B"),
                Color.parseColor("#FF8EEE"),
                Color.parseColor("#000000")
        )

        val colorsAdapter = ColorListAdapter(colors)
        bottomsheet_rv_note_color.apply {
            adapter = colorsAdapter
            layoutManager = GridLayoutManager(activity!!, 6)
            addItemDecoration(itemDecoration)
        }
    }

    private fun setupTextColorRecyclerView() {
        val colors = listOf(
                Color.parseColor("#000000"),
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#FF6464"),
                Color.parseColor("#FDEC61"),
                Color.parseColor("#76A0FF"),
                Color.parseColor("#FFFFFF"),
        )

        val colorsAdapter = ColorListAdapter(colors)
        bottomsheet_rv_text_color.apply {
            adapter = colorsAdapter
            layoutManager = GridLayoutManager(activity!!, 6)
            addItemDecoration(itemDecoration)
        }
    }

}