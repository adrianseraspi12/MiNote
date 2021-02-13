package com.suzei.minote.ui.editor.note


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.ext.convertToPx
import com.suzei.minote.ext.moveFocus
import com.suzei.minote.ext.setAlpha
import com.suzei.minote.ext.showColorWheel
import com.suzei.minote.utils.Turing
import com.suzei.minote.utils.dialogs.PasswordDialog
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapter
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapterBuilder
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapterCallback
import com.suzei.minote.utils.recycler_view.decorator.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.bottomsheet_edit_note.*
import kotlinx.android.synthetic.main.fragment_editor.*

class EditorNoteFragment : Fragment(), EditorNoteContract.View {

    private lateinit var presenter: EditorNoteContract.Presenter

    private var mPassword: String? = null
    private var currentNoteColor = -1
    private var currentTextColor = -1
    private lateinit var noteColorsAdapter: ColorListAdapter
    private lateinit var textColorsAdapter: ColorListAdapter
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
        initAdapters()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_editor, container, false)

        if (savedInstanceState != null) {
            currentNoteColor = savedInstanceState.getInt(EXTRA_NOTE_COLOR, -1)
            currentTextColor = savedInstanceState.getInt(EXTRA_TEXT_COLOR, -1)
            mPassword = savedInstanceState.getString(EXTRA_NOTE_COLOR, null)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setup()
        setupBottomSheet()
        setupSaveOnClick()
        setupBack()
        setupLock()
        setupNoteColorRecyclerView()
        setupTextColorRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        if (currentNoteColor != -1 || currentTextColor != -1) {
            setNoteColor(currentNoteColor)
            setTextColor(currentTextColor)
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
        mPassword = note.password?.let { Turing.decrypt(it) }
        editor_title.setText(note.title)
        editor_text.setText(note.message)
        setNoteColor(Color.parseColor(note.color))
        setTextColor(Color.parseColor(note.textColor))

        editor_title.moveFocus()
    }

    override fun setNoteColor(noteColor: Int) {
        currentNoteColor = noteColor
        activity?.window?.statusBarColor = noteColor
        editor_root.setBackgroundColor(noteColor)
        noteColorsAdapter.setSelectedColor(noteColor)
    }

    override fun setTextColor(textColor: Int) {
        currentTextColor = textColor
        editor_title.setTextColor(textColor)
        editor_text.setTextColor(textColor)
        editor_back_arrow.setColorFilter(textColor)
        editor_title.setHintTextColor(textColor.setAlpha(0.5f))
        editor_text.setHintTextColor(textColor.setAlpha(0.5f))
        textColorsAdapter.setSelectedColor(textColor)
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupBack() {
        editor_back_arrow.setOnClickListener {
            activity!!.finish()
        }
    }

    private fun setupNoteColorRecyclerView() {
        bottomsheet_rv_note_color.apply {
            adapter = noteColorsAdapter
            layoutManager = GridLayoutManager(activity!!, 6)
            addItemDecoration(itemDecoration)
        }
    }

    private fun setupTextColorRecyclerView() {
        bottomsheet_rv_text_color.apply {
            adapter = textColorsAdapter
            layoutManager = GridLayoutManager(activity!!, 6)
            addItemDecoration(itemDecoration)
        }
    }

    private fun setupLock() {
        bottom_sheet_switch_lock.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                val passwordDialog = PasswordDialog.instance(mPassword ?: "") {
                    if (it.isEmpty()) {
                        bottom_sheet_switch_lock.isChecked = false
                    } else {
                        mPassword = it
                    }
                }
                passwordDialog.show(fragmentManager!!, "Password Dialog")
            } else {
                mPassword = null
            }
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
        val hiddenView = bottomsheet_settings_container.getChildAt(2)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    //  Set Editor (EditText) a margin to avoid
                    //  bottomsheet overlaps the editor
                    val params = CoordinatorLayout.LayoutParams(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.MATCH_PARENT
                    )
                    val bottomsheetSize = hiddenView.top + 32.convertToPx(resources)
                    params.setMargins(0, 56.convertToPx(resources),
                            0, bottomsheetSize)

                    editor_edittext_container.layoutParams = params
                    editor_edittext_container.requestLayout()
                    bottomSheetBehavior.removeBottomSheetCallback(this)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })

        bottomsheet_settings_container.viewTreeObserver.addOnGlobalLayoutListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.setPeekHeight(hiddenView.top, true)
        }
    }

    private fun initAdapters() {
        noteColorsAdapter = ColorListAdapterBuilder.noteList(object : ColorListAdapterCallback {

            override fun onChangedColor(color: Int) {
                setNoteColor(color)
            }

            override fun onShowColorWheel(color: Int) {
                showColorWheel("Choose note color", color) {
                    setNoteColor(it)
                }
            }

        })

        textColorsAdapter = ColorListAdapterBuilder.textList(object : ColorListAdapterCallback {
            override fun onChangedColor(color: Int) {
                setTextColor(color)
            }

            override fun onShowColorWheel(color: Int) {
                showColorWheel("Choose text color", color) {
                    setTextColor(it)
                }
            }

        })
    }
}