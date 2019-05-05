package com.suzei.minote.ui.editor.todo


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suzei.minote.R
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.utils.ColorWheel
import com.suzei.minote.utils.dialogs.BottomSheetFragment
import com.suzei.minote.utils.dialogs.InputDialog
import com.suzei.minote.utils.dialogs.InputDialogListener
import kotlinx.android.synthetic.main.fragment_editor.*
import kotlinx.android.synthetic.main.fragment_editor_todo.*
import kotlinx.android.synthetic.main.item_row_edit_todo.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class EditorTodoFragment : Fragment(), View.OnClickListener, EditorTodoContract.View {

    companion object {

        internal fun newInstance(): EditorTodoFragment {
            return EditorTodoFragment()
        }

        val INPUT_DIALOG_TAG = "INPUT_DIALOG_TAG"

    }

    private lateinit var presenter: EditorTodoContract.Presenter

    private lateinit var todoItemList: MutableList<TodoItem>

    private lateinit var todoItemListAdapter: TodoItemAdapter

    private val inputDialog = InputDialog.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        todoItemList = ArrayList()
        todoItemListAdapter = TodoItemAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editor_todo_add_item.setOnClickListener(this)
        editor_todo_save.setOnClickListener(this)
        editor_todo_menu.setOnClickListener(this)
        editor_todo_back_arrow.setOnClickListener(this)

        editor_todo_list.layoutManager = LinearLayoutManager(context)
        editor_todo_list.adapter = todoItemListAdapter
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun setPresenter(presenter: EditorTodoContract.Presenter) {
        this.presenter = presenter
    }

    override fun showTodoDetails(todo: Todo) {
        todoItemList = todo.todoItems?.toMutableList() ?: ArrayList()
        todoItemListAdapter.notifyDataSetChanged()

        editor_todo_title.setText(todo.title)
        noteColor(Color.parseColor(todo.color))
        textColor(Color.parseColor(todo.textColor))
    }

    override fun showAddTask(todoItem: TodoItem) {
        todoItemList.add(todoItem)
        todoItemListAdapter.notifyDataSetChanged()
    }

    override fun showUpdatedTask(position: Int, todoItem: TodoItem) {
        todoItemList[position] = todoItem
        todoItemListAdapter.notifyDataSetChanged()
    }

    override fun showAddItemDialog() {
        inputDialog.show(fragmentManager!!, INPUT_DIALOG_TAG)
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

    override fun noteColor(noteColor: Int) {
        editor_todo_root.setBackgroundColor(noteColor)
    }

    override fun textColor(textColor: Int) {
        editor_todo_title.setTextColor(textColor)
        editor_todo_back_arrow.setColorFilter(textColor)
        editor_todo_save.setColorFilter(textColor)
        editor_todo_menu.setColorFilter(textColor)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.editor_todo_back_arrow -> activity!!.finish()

            R.id.editor_todo_menu -> showMenu()

            R.id.editor_todo_add_item -> addItem()

            R.id.editor_todo_save -> saveTodo()


        }

    }

    private fun saveTodo() {
        val noteColor = (editor_todo_root.background as ColorDrawable).color
        val hexNoteColor = String.format("#%06X", 0xFFFFFF and noteColor)

        val textColor = editor_todo_title.currentTextColor
        val hexTextColor = String.format("#%06X", 0xFFFFFF and textColor)

        val title = editor_todo_title.text.toString()
        presenter.saveTodo(title, todoItemList, hexNoteColor, hexTextColor)
    }

    private fun showMenu() {
        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.setPasswordVisibility(false)
        bottomSheetFragment.retainInstance = true
        bottomSheetFragment.setClickListener(object: BottomSheetFragment.ClickListener {

            override fun onEditPasswordClick() {
                Toast.makeText(context, "Edit Password", Toast.LENGTH_SHORT).show()
            }

            override fun onChangeNoteColorClick() {
                val noteColor = (editor_todo_root.background as ColorDrawable).color
                presenter.noteColorWheel(noteColor)
                bottomSheetFragment.dismiss()
            }

            override fun onChangeTextColorClick() {
                val textColor = editor_todo_title.currentTextColor
                presenter.textColorWheel(textColor)
                bottomSheetFragment.dismiss()
            }

        })

        bottomSheetFragment.show(fragmentManager!!, bottomSheetFragment.tag)
    }

    private fun addItem() {
        inputDialog.title = "Add Task"
        inputDialog.actionTitle = "Add"
        inputDialog.removeOnAddClickListener()
        inputDialog.setOnAddClickListener(object: InputDialogListener {

            override fun onAddClick(message: String?) {

                if (message!!.isNotEmpty()) {
                    presenter.addTask(message)
                }

            }

        })

        showAddItemDialog()
    }

    inner class TodoItemAdapter: RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
            val view = layoutInflater.inflate(R.layout.item_row_edit_todo, parent, false)
            return TodoItemViewHolder(view)
        }

        override fun getItemCount(): Int {
            return todoItemList.size
        }

        override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
            val todoItem = todoItemList[position]
            holder.bind(todoItem)
        }


        inner class TodoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(todoItem: TodoItem) {
                val position = adapterPosition + 1

                itemView.item_edit_todo_text.text = todoItem.task
                itemView.item_edit_todo_number.text = "$position.)"

                itemView.item_edit_todo_edit.setOnClickListener {
                    inputDialog.message = todoItem.task!!
                    inputDialog.title = "Edit Task"
                    inputDialog.actionTitle = "Edit"
                    inputDialog.removeOnAddClickListener()
                    inputDialog.setOnAddClickListener(object: InputDialogListener {

                        override fun onAddClick(message: String?) {

                            if (message!!.isNotEmpty()) {
                                todoItem.task = message
                                presenter.updateTask(adapterPosition, todoItem)
                            }

                        }

                    })

                    showAddItemDialog()
                }

            }

        }

    }

}
