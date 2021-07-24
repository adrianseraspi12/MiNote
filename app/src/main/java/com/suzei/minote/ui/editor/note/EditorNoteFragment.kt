package com.suzei.minote.ui.editor.note

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.suzei.minote.data.local.entity.Notes
import com.suzei.minote.databinding.BottomsheetEditNoteBinding
import com.suzei.minote.databinding.FragmentEditorBinding
import com.suzei.minote.ext.moveFocus
import com.suzei.minote.ext.setAlpha
import com.suzei.minote.ui.editor.BaseEditorFragment
import com.suzei.minote.utils.Turing
import com.suzei.minote.utils.dialogs.PasswordDialog

class EditorNoteFragment : BaseEditorFragment(), EditorNoteContract.View {

    private lateinit var presenter: EditorNoteContract.Presenter

    private var mPassword: String? = null
    private var currentNoteColor = -1
    private var currentTextColor = -1

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
        setupBottomSheet(binding.bottomsheetEditNote.bottomsheetSettingsContainer, binding.editorEdittextContainer)
        setupSaveOnClick()
        setupEditTextTextWatcher()
        setupBack(binding.editorBackArrow)
        setupLock()
        setupNoteColorRecyclerView(bottomsheetEditNoteBinding.bottomsheetRvNoteColor)
        setupTextColorRecyclerView(bottomsheetEditNoteBinding.bottomsheetRvTextColor)
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

    override fun onAfterTextChanged() {
        requestAutoSave()
    }

    override fun onChangeNoteColor(color: Int) {
        setNoteColor(color)
        requestAutoSave()
    }

    override fun onChangeTextColor(color: Int) {
        setTextColor(color)
        requestAutoSave()
    }

    override fun onShowNoteColorWheel(color: Int) {
        setNoteColor(color)
        requestAutoSave()
    }

    override fun onShowTextColorWheel(color: Int) {
        setTextColor(color)
        requestAutoSave()
    }

    override fun setPresenter(presenter: EditorNoteContract.Presenter) {
        this.presenter = presenter
    }

    override fun showDetails(data: Notes) {
        mPassword = data.password?.let { Turing.decrypt(it) }
        binding.editorTitle.setText(data.title)
        binding.editorText.setText(data.message)
        setNoteColor(Color.parseColor(data.color))
        setTextColor(Color.parseColor(data.textColor))

        binding.editorTitle.moveFocus()
    }

    override fun setNoteColor(color: Int) {
        currentNoteColor = color
        activity?.window?.statusBarColor = color
        binding.editorRoot.setBackgroundColor(color)
        noteColorsAdapter.setSelectedColor(color)
    }

    override fun setTextColor(color: Int) {
        currentTextColor = color
        binding.editorTitle.setTextColor(color)
        binding.editorText.setTextColor(color)
        binding.editorBackArrow.setColorFilter(color)
        binding.editorTitle.setHintTextColor(color.setAlpha(0.5f))
        binding.editorText.setHintTextColor(color.setAlpha(0.5f))
        textColorsAdapter.setSelectedColor(color)
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

    private fun setupEditTextTextWatcher() {
        binding.editorText.addTextChangedListener(setTextWatcher(binding.editorText))
        binding.editorTitle.addTextChangedListener(setTextWatcher(binding.editorTitle))
    }

    private fun setupLock() {
        bottomsheetEditNoteBinding.bottomSheetSwitchLock.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                val passwordDialog = PasswordDialog.instance(mPassword ?: "") {
                    if (it.isEmpty()) {
                        bottomsheetEditNoteBinding.bottomSheetSwitchLock.isChecked = false
                    } else {
                        mPassword = it
                        requestAutoSave()
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

    private fun requestAutoSave() {
        val noteColor = (binding.editorRoot.background as ColorDrawable).color
        val hexNoteColor = String.format("#%06X", 0xFFFFFF and noteColor)

        val textColor = binding.editorText.currentTextColor
        val hexTextColor = String.format("#%06X", 0xFFFFFF and textColor)

        val title = binding.editorTitle.text.toString()
        val message = binding.editorText.text.toString()
        val password = mPassword?.let { it1 -> Turing.encrypt(it1) }

        presenter.autoSave(title, message, hexNoteColor, hexTextColor, password)
    }
}