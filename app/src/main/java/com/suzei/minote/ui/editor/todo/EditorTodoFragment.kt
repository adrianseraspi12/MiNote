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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.suzei.minote.R
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.ext.*
import com.suzei.minote.utils.dialogs.PasswordDialog
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapter
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapterBuilder
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapterCallback
import com.suzei.minote.utils.recycler_view.decorator.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.bottomsheet_edit_note.*
import kotlinx.android.synthetic.main.fragment_editor_todo.*

class EditorTodoFragment : Fragment(), EditorTodoContract.View {

    companion object {

        internal fun newInstance(): EditorTodoFragment {
            return EditorTodoFragment()
        }

    }

    private lateinit var presenter: EditorTodoContract.Presenter
    private var mPassword: String? = null
    private var isNewSubtaskDone = false
    private var textColor: Int = 0x000000
    private var taskCount = 0
    private lateinit var noteColorsAdapter: ColorListAdapter
    private lateinit var textColorsAdapter: ColorListAdapter
    private lateinit var itemDecoration: GridSpacingItemDecoration
    private lateinit var todoSubtaskAdapter: TodoSubtaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        itemDecoration = GridSpacingItemDecoration(
                6,
                resources.getDimensionPixelSize(R.dimen.colorListSpacing),
                false
        )
        initAdapters()
        todoSubtaskAdapter = TodoSubtaskAdapter(ArrayList()) {
            taskCount--
            setTaskCount()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.start()
        setupBottomSheet()
        setupNoteColorRecyclerView()
        setupTextColorRecyclerView()
        setupLock()
        setupSubTaskRecyclerView()
        editor_todo_cv_done.setOnClickListener(onNewSubTaskDoneClickListener)
        editor_todo_tv_text.addTextChangedListener(onAddSubTaskTextChangedListener)
        item_editor_todo_add.setOnClickListener(onAddNewSubTaskClickListener)
        editor_todo_save.setOnClickListener(onSaveClickListener)
        editor_todo_back_arrow.setOnClickListener(onBackClickListener)
    }

    override fun setPresenter(presenter: EditorTodoContract.Presenter) {
        this.presenter = presenter
    }

    override fun showTodoDetails(todo: Todo) {
        val todoItemList = todo.todoItems?.toMutableList() ?: ArrayList()
        taskCount = todoItemList.size
        todoSubtaskAdapter.updateData(todoItemList)
        setTaskCount()

        editor_todo_title.setText(todo.title)
        setNoteColor(Color.parseColor(todo.color))
        setTextColor(Color.parseColor(todo.textColor))

        editor_todo_title.moveFocus()
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun setNoteColor(color: Int) {
        editor_todo_root.setBackgroundColor(color)
    }

    override fun setTextColor(color: Int) {
        this.textColor = color
        todoSubtaskAdapter.change(textColor)

        editor_todo_back_arrow.setColorFilter(textColor)
        editor_todo_iv_addsubtask.setColorFilter(textColor)
        item_editor_todo_add.setColorFilter(textColor)
        editor_todo_divider.setBackgroundColor(textColor.setAlpha(0.5f))
        editor_todo_cv_done.strokeColor = textColor
        editor_todo_title.setTextColor(textColor)
        editor_todo_tv_subtask_count.setTextColor(textColor)
        editor_todo_tv_subtask_title.setTextColor(textColor)
        editor_todo_tv_text.setTextColor(textColor)
        if (isNewSubtaskDone) {
            editor_todo_cv_done.setCardBackgroundColor(textColor)
        }
    }

    private var onBackClickListener = View.OnClickListener { activity!!.finish() }

    private var onSaveClickListener = View.OnClickListener {
        val noteColor = (editor_todo_root.background as ColorDrawable).color
        val hexNoteColor = String.format("#%06X", 0xFFFFFF and noteColor)

        val textColor = editor_todo_title.currentTextColor
        val hexTextColor = String.format("#%06X", 0xFFFFFF and textColor)

        val title = editor_todo_title.text.toString()
        val todoItemList = todoSubtaskAdapter.data
        presenter.saveTodo(title, todoItemList, hexNoteColor, hexTextColor)
    }

    private var onAddSubTaskTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val charSequence = p0 ?: ""
            if (charSequence.isNotEmpty()) {
                editor_todo_iv_addsubtask.visibility = View.INVISIBLE
                item_editor_todo_add.visibility = View.VISIBLE
                editor_todo_cv_done.visibility = View.VISIBLE
            } else {
                editor_todo_iv_addsubtask.visibility = View.VISIBLE
                item_editor_todo_add.visibility = View.GONE
                editor_todo_cv_done.visibility = View.GONE
            }
        }

    }

    private var onNewSubTaskDoneClickListener = View.OnClickListener {
        isNewSubtaskDone = if (isNewSubtaskDone) {
            val transparent = ContextCompat.getColor(context!!, android.R.color.transparent)
            editor_todo_cv_done.setCardBackgroundColor(transparent)
            false
        } else {
            editor_todo_cv_done.setCardBackgroundColor(textColor)
            true
        }
    }

    private var onAddNewSubTaskClickListener = View.OnClickListener {
        val text = editor_todo_tv_text.text.toString()
        val todoItem = TodoItem(text, isNewSubtaskDone)
        val transparent = ContextCompat.getColor(context!!, android.R.color.transparent)

        todoSubtaskAdapter.add(todoItem)
        taskCount++

        setTaskCount()

        editor_todo_tv_text.text = null
        isNewSubtaskDone = false
        editor_todo_cv_done.setCardBackgroundColor(transparent)
        editor_todo_tv_text.clearFocus()
        hideKeyboard()
        editor_todo_scroll_view.scrollToBottom()
    }

    private fun setupSubTaskRecyclerView() {
        editor_todo_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todoSubtaskAdapter
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

    @SuppressLint("SetTextI18n")
    private fun setTaskCount() {
        if (taskCount == 1) {
            editor_todo_tv_subtask_count.text = "$taskCount Task"
        } else {
            editor_todo_tv_subtask_count.text = "$taskCount Tasks"
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

    private fun initAdapters() {
        noteColorsAdapter = ColorListAdapterBuilder.noteList(object : ColorListAdapterCallback {

            override fun onChangedColor(color: Int) {
                setNoteColor(color)
            }

            override fun onShowColorWheel(color: Int) {
                showColorWheel("Choose note color", color) {
                    setNoteColor(it)
                    noteColorsAdapter.setSelectedColor(it)
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
                    textColorsAdapter.setSelectedColor(it)
                }
            }

        })
    }

    private fun setupBottomSheet() {
        bottom_sheet_switch_lock.visibility = View.GONE
        bottomsheet_lock_title.visibility = View.GONE
        bottomsheet_divider_bottom.visibility = View.GONE

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

                    editor_todo_container.layoutParams = params
                    editor_todo_container.requestLayout()
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
}
