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
import com.suzei.minote.databinding.BottomsheetEditNoteBinding
import com.suzei.minote.databinding.FragmentEditorBinding
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

class EditorNoteFragment : Fragment(), EditorNoteContract.View {

    private lateinit var presenter: EditorNoteContract.Presenter

    private var mPassword: String? = null
    private var currentNoteColor = -1
    private var currentTextColor = -1
    private lateinit var noteColorsAdapter: ColorListAdapter
    private lateinit var textColorsAdapter: ColorListAdapter
    private lateinit var itemDecoration: GridSpacingItemDecoration

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private var _bottomsheetEditNoteBinding: BottomsheetEditNoteBinding? = null
    private val bottomsheetEditNoteBinding get() = _bottomsheetEditNoteBinding!!

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
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        _bottomsheetEditNoteBinding = binding.bottomsheetEditNote
        if (savedInstanceState != null) {
            currentNoteColor = savedInstanceState.getInt(EXTRA_NOTE_COLOR, -1)
            currentTextColor = savedInstanceState.getInt(EXTRA_TEXT_COLOR, -1)
            mPassword = savedInstanceState.getString(EXTRA_NOTE_COLOR, null)
        }

        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val noteColor = (binding.editorRoot.background as ColorDrawable).color
        val textColor = binding.editorText.currentTextColor
        outState.putString(EXTRA_PASSWORD, mPassword)
        outState.putInt(EXTRA_NOTE_COLOR, noteColor)
        outState.putInt(EXTRA_TEXT_COLOR, textColor)
    }

    override fun setPresenter(presenter: EditorNoteContract.Presenter) {
        this.presenter = presenter
    }

    override fun showNoteDetails(note: Notes) {
        mPassword = note.password?.let { Turing.decrypt(it) }
        binding.editorTitle.setText(note.title)
        binding.editorText.setText(note.message)
        setNoteColor(Color.parseColor(note.color))
        setTextColor(Color.parseColor(note.textColor))

        binding.editorTitle.moveFocus()
    }

    override fun setNoteColor(noteColor: Int) {
        currentNoteColor = noteColor
        activity?.window?.statusBarColor = noteColor
        binding.editorRoot.setBackgroundColor(noteColor)
        noteColorsAdapter.setSelectedColor(noteColor)
    }

    override fun setTextColor(textColor: Int) {
        currentTextColor = textColor
        binding.editorTitle.setTextColor(textColor)
        binding.editorText.setTextColor(textColor)
        binding.editorBackArrow.setColorFilter(textColor)
        binding.editorTitle.setHintTextColor(textColor.setAlpha(0.5f))
        binding.editorText.setHintTextColor(textColor.setAlpha(0.5f))
        textColorsAdapter.setSelectedColor(textColor)
    }

    override fun setSaveBtnVisibility(isAutoSaveEnable: Boolean) {
        if (isAutoSaveEnable) {
            binding.editorBtnSave.visibility = View.GONE
        } else {
            binding.editorBtnSave.visibility = View.VISIBLE
        }
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupBack() {
        binding.editorBackArrow.setOnClickListener {
            activity!!.finish()
        }
    }

    private fun setupNoteColorRecyclerView() {
        bottomsheetEditNoteBinding.bottomsheetRvNoteColor.apply {
            adapter = noteColorsAdapter
            layoutManager = GridLayoutManager(activity!!, 6)
            addItemDecoration(itemDecoration)
        }
    }

    private fun setupTextColorRecyclerView() {
        bottomsheetEditNoteBinding.bottomsheetRvTextColor.apply {
            adapter = textColorsAdapter
            layoutManager = GridLayoutManager(activity!!, 6)
            addItemDecoration(itemDecoration)
        }
    }

    private fun setupLock() {
        bottomsheetEditNoteBinding.bottomSheetSwitchLock.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                val passwordDialog = PasswordDialog.instance(mPassword ?: "") {
                    if (it.isEmpty()) {
                        bottomsheetEditNoteBinding.bottomSheetSwitchLock.isChecked = false
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
        binding.editorBtnSave.setOnClickListener {
            val noteColor = (binding.editorRoot.background as ColorDrawable).color
            val hexNoteColor = String.format("#%06X", 0xFFFFFF and noteColor)

            val textColor = binding.editorText.currentTextColor
            val hexTextColor = String.format("#%06X", 0xFFFFFF and textColor)

            val title = binding.editorTitle.text.toString()
            val message = binding.editorText.text.toString()
            val password = mPassword?.let { it1 -> Turing.encrypt(it1) }

            presenter.saveNote(title, message, hexNoteColor, hexTextColor, password)
        }
    }

    private fun setupBottomSheet() {
        //  Setup Bottomsheet Behavior
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomsheetEditNote.bottomsheetSettingsContainer)
        val hiddenView = binding.bottomsheetEditNote.bottomsheetSettingsContainer.getChildAt(2)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    //  Set Editor (EditText) a margin to avoid
                    //  bottomsheet overlaps the editor
                    val params = CoordinatorLayout.LayoutParams(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.MATCH_PARENT
                    )
                    val bottomsheetSize = hiddenView.top + 56.convertToPx(resources)
                    params.setMargins(0, 56.convertToPx(resources),
                            0, bottomsheetSize)

                    binding.editorEdittextContainer.layoutParams = params
                    binding.editorEdittextContainer.requestLayout()
                    bottomSheetBehavior.removeBottomSheetCallback(this)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })

        binding.bottomsheetEditNote.bottomsheetSettingsContainer.viewTreeObserver.addOnGlobalLayoutListener {
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