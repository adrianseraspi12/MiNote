package com.suzei.minote.utils.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.suzei.minote.R
import com.suzei.minote.databinding.DialogCreateNoteBinding

class SelectNoteDialog : BottomSheetDialogFragment() {

    private var selectNoteDialogListener: ((Int) -> Unit)? = null
    private var currentSelectedCatagory = NOTE_PAD

    private var _binding: DialogCreateNoteBinding? = null
    private val binding get() = _binding!!

    companion object {

        const val NOTE_PAD = 0
        const val TODO_LIST = 1
        const val TAG = "SELECT_NOTE_DIALOG"

        fun newInstance(selectNoteDialogListener: ((Int) -> Unit)?): SelectNoteDialog {
            val fragment = SelectNoteDialog()
            fragment.selectNoteDialogListener = selectNoteDialogListener
            return fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCreateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {
        val primaryColor = ResourcesCompat.getColor(resources, R.color.primaryColor, null)
        val secondaryColor = ResourcesCompat.getColor(resources, R.color.secondaryColor, null)

        binding.createNoteContainerNote.setOnClickListener {
            changeCategoryColor(secondaryColor, primaryColor)
            currentSelectedCatagory = NOTE_PAD
        }

        binding.createNoteContainerTodo.setOnClickListener {
            changeCategoryColor(primaryColor, secondaryColor)
            currentSelectedCatagory = TODO_LIST
        }

        binding.createNoteBtnCreate.setOnClickListener {
            selectNoteDialogListener?.invoke(currentSelectedCatagory)
        }
    }

    private fun changeCategoryColor(noteColor: Int, todoColor: Int) {
        binding.createNoteContainerNote.strokeColor = noteColor
        binding.createNoteContainerTodo.strokeColor = todoColor
        binding.createNoteContainerNote.invalidate()
        binding.createNoteContainerTodo.invalidate()
    }

    override fun dismiss() {
        currentSelectedCatagory = NOTE_PAD
        super.dismiss()
    }
}