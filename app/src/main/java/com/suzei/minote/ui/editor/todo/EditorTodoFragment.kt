package com.suzei.minote.ui.editor.todo

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.local.entity.TodoItem
import com.suzei.minote.databinding.BottomsheetEditNoteBinding
import com.suzei.minote.databinding.FragmentEditorTodoBinding
import com.suzei.minote.ext.hideKeyboard
import com.suzei.minote.ext.moveFocus
import com.suzei.minote.ext.scrollToBottom
import com.suzei.minote.ext.setAlpha
import com.suzei.minote.ui.editor.BaseEditorFragment

class EditorTodoFragment : BaseEditorFragment(), EditorTodoContract.View {

    companion object {

        private const val EXTRA_NOTE_COLOR = "EXTRA_NOTE_COLOR"
        private const val EXTRA_TEXT_COLOR = "EXTRA_TEXT_COLOR"

        internal fun newInstance(): EditorTodoFragment {
            return EditorTodoFragment()
        }

    }

    private lateinit var presenter: EditorTodoContract.Presenter
    private var isNewSubtaskDone = false
    private var currentNoteColor = -1
    private var currentTextColor = -1
    private var taskCount = 0
    private lateinit var todoSubtaskAdapter: TodoSubtaskAdapter
    private var _binding: FragmentEditorTodoBinding? = null
    private val binding get() = _binding!!

    private var _bottomsheetEditNoteBinding: BottomsheetEditNoteBinding? = null
    private val bottomsheetEditNoteBinding get() = _bottomsheetEditNoteBinding!!

    private var onChangesCallback: () -> Unit = { requestAutoSave() }
    private var onDeleteCalback: () -> Unit = {
        taskCount--
        setTaskCount()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoSubtaskAdapter = TodoSubtaskAdapter(ArrayList(), onChangesCallback, onDeleteCalback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditorTodoBinding.inflate(inflater, container, false)
        _bottomsheetEditNoteBinding = binding.bottomsheetEditNote

        if (savedInstanceState != null) {
            currentNoteColor = savedInstanceState.getInt(EXTRA_NOTE_COLOR, -1)
            currentTextColor = savedInstanceState.getInt(EXTRA_TEXT_COLOR, -1)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setup()
        setupBottomSheet(binding.bottomsheetEditNote.bottomsheetSettingsContainer, binding.editorTodoContainer)
        setupNoteColorRecyclerView(bottomsheetEditNoteBinding.bottomsheetRvNoteColor)
        setupTextColorRecyclerView(bottomsheetEditNoteBinding.bottomsheetRvTextColor)
        setupSubTaskRecyclerView()
        setupBack(binding.editorTodoBackArrow)
        hideLock()
        binding.editorTodoTitle.addTextChangedListener(setTextWatcher(binding.editorTodoTitle))
        binding.editorTodoCvDone.setOnClickListener(onNewSubTaskDoneClickListener)
        binding.editorTodoTvText.addTextChangedListener(onAddSubTaskTextChangedListener)
        binding.itemEditorTodoAdd.setOnClickListener(onAddNewSubTaskClickListener)
        binding.editorTodoSave.setOnClickListener(onSaveClickListener)
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
        _bottomsheetEditNoteBinding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_NOTE_COLOR, currentNoteColor)
        outState.putInt(EXTRA_TEXT_COLOR, currentTextColor)
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

    override fun setPresenter(presenter: EditorTodoContract.Presenter) {
        this.presenter = presenter
    }

    override fun showDetails(data: Todo) {
        val todoItemList = data.todoItems?.toMutableList() ?: ArrayList()
        taskCount = todoItemList.size
        todoSubtaskAdapter.updateData(todoItemList)
        setTaskCount()

        binding.editorTodoTitle.setText(data.title)
        setNoteColor(Color.parseColor(data.color))
        setTextColor(Color.parseColor(data.textColor))

        binding.editorTodoTitle.moveFocus()
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun setNoteColor(color: Int) {
        this.currentNoteColor = color
        activity?.window?.statusBarColor = color
        binding.editorTodoRoot.setBackgroundColor(color)
        todoSubtaskAdapter.changeBackgroundColor(color)
        noteColorsAdapter.setSelectedColor(color)
        binding.editorTodoIvCheck.setColorFilter(color)
    }

    override fun setSaveBtnVisibility(isAutoSaveEnable: Boolean) {
        if (isAutoSaveEnable) {
            binding.editorTodoSave.visibility = View.GONE
        } else {
            binding.editorTodoSave.visibility = View.VISIBLE
        }
    }

    override fun setTextColor(color: Int) {
        this.currentTextColor = color
        todoSubtaskAdapter.changeTextColor(color)

        binding.editorTodoBackArrow.setColorFilter(color)
        binding.editorTodoIvAddsubtask.setColorFilter(color)
        binding.itemEditorTodoAdd.setColorFilter(color)
        binding.editorTodoDivider.setBackgroundColor(color.setAlpha(0.5f))
        binding.editorTodoCvDone.strokeColor = color
        binding.editorTodoTitle.setTextColor(color)
        binding.editorTodoTitle.setHintTextColor(color.setAlpha(0.5f))
        binding.editorTodoTvSubtaskCount.setTextColor(color)
        binding.editorTodoTvSubtaskTitle.setTextColor(color)
        binding.editorTodoTvText.setTextColor(color)
        binding.editorTodoTvText.setHintTextColor(color.setAlpha(0.5f))
        if (isNewSubtaskDone) {
            binding.editorTodoCvDone.setCardBackgroundColor(color)
        }
        textColorsAdapter.setSelectedColor(color)
    }

    private var onSaveClickListener = View.OnClickListener {
        val noteColor = (binding.editorTodoRoot.background as ColorDrawable).color
        val hexNoteColor = String.format("#%06X", 0xFFFFFF and noteColor)

        val textColor = binding.editorTodoTitle.currentTextColor
        val hexTextColor = String.format("#%06X", 0xFFFFFF and textColor)

        val title = binding.editorTodoTitle.text.toString()
        val todoItemList = todoSubtaskAdapter.data
        presenter.saveTodo(title, todoItemList, hexNoteColor, hexTextColor)
    }

    private var onAddSubTaskTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val charSequence = p0 ?: ""
            if (charSequence.isNotEmpty()) {
                binding.editorTodoIvAddsubtask.visibility = View.GONE
                binding.itemEditorTodoAdd.visibility = View.VISIBLE
                binding.editorTodoCvDone.visibility = View.VISIBLE
            } else {
                binding.editorTodoIvAddsubtask.visibility = View.VISIBLE
                binding.itemEditorTodoAdd.visibility = View.GONE
                binding.editorTodoCvDone.visibility = View.GONE
            }
        }

    }

    private var onNewSubTaskDoneClickListener = View.OnClickListener {
        isNewSubtaskDone = if (isNewSubtaskDone) {
            val transparent = ContextCompat.getColor(context!!, android.R.color.transparent)
            binding.editorTodoCvDone.setCardBackgroundColor(transparent)
            binding.editorTodoIvCheck.visibility = View.GONE
            false
        } else {
            binding.editorTodoCvDone.setCardBackgroundColor(currentTextColor)
            binding.editorTodoIvCheck.visibility = View.VISIBLE
            true
        }
    }

    private var onAddNewSubTaskClickListener = View.OnClickListener {
        val text = binding.editorTodoTvText.text.toString()
        val todoItem = TodoItem(text, isNewSubtaskDone)
        val transparent = ContextCompat.getColor(context!!, android.R.color.transparent)

        todoSubtaskAdapter.add(todoItem)
        taskCount++

        setTaskCount()

        binding.editorTodoTvText.text = null
        isNewSubtaskDone = false
        binding.editorTodoCvDone.setCardBackgroundColor(transparent)
        binding.editorTodoTvText.clearFocus()
        hideKeyboard()
        binding.editorTodoScrollView.scrollToBottom()
        requestAutoSave()
    }

    private fun hideLock() {
        bottomsheetEditNoteBinding.bottomSheetSwitchLock.visibility = View.GONE
        bottomsheetEditNoteBinding.bottomsheetLockTitle.visibility = View.GONE
        bottomsheetEditNoteBinding.bottomsheetDividerBottom.visibility = View.GONE
    }

    private fun setupSubTaskRecyclerView() {
        binding.editorTodoList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoSubtaskAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTaskCount() {
        if (taskCount == 1) {
            binding.editorTodoTvSubtaskCount.text = "$taskCount Task"
        } else {
            binding.editorTodoTvSubtaskCount.text = "$taskCount Tasks"
        }
    }

    private fun requestAutoSave() {
        val noteColor = (binding.editorTodoRoot.background as ColorDrawable).color
        val hexNoteColor = String.format("#%06X", 0xFFFFFF and noteColor)

        val textColor = binding.editorTodoTitle.currentTextColor
        val hexTextColor = String.format("#%06X", 0xFFFFFF and textColor)

        val title = binding.editorTodoTitle.text.toString()
        val todoItemList = todoSubtaskAdapter.data
        presenter.autoSave(title, todoItemList, hexNoteColor, hexTextColor)
    }
}
