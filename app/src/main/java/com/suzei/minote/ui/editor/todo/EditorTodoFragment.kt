package com.suzei.minote.ui.editor.todo


import android.graphics.Color
import android.graphics.Paint
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.suzei.minote.R
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.ext.moveFocus
import com.suzei.minote.utils.ColorWheel
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.dialogs.BottomSheetFragment
import com.suzei.minote.utils.dialogs.InputDialog
import com.suzei.minote.utils.dialogs.InputDialogListener
import kotlinx.android.synthetic.main.fragment_editor_todo.*
import kotlinx.android.synthetic.main.item_row_edit_todo.view.*
import kotlinx.android.synthetic.main.item_row_notes_default.view.*

/**
 * A simple [Fragment] subclass.
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        todoItemList = ArrayList()
        todoItemListAdapter = TodoItemAdapter(object : TodoAdapterClickListener {

            override fun onEditClick(position: Int) {
                val todoItem = todoItemList[position]

                showAddItemDialog(
                        "Edit Task",
                        "Edit",
                        todoItem.task!!,
                        object: InputDialogListener {

                            override fun onAddClick(message: String?) {

                                val itemPosition = todoItemList.indexOf(todoItem)

                                if (message!!.isNotEmpty()) {
                                    todoItem.task = message
                                    presenter.updateTask(itemPosition, todoItem)
                                }

                            }

                        })
            }

            override fun onDeleteClick(position: Int) {
                todoItemList.removeAt(position)
                todoItemListAdapter.notifyDataSetChanged()
                editor_todo_list.smoothScrollToPosition(position)
            }

            override fun onItemClick(position: Int) {
                val todoItem = todoItemList[position]

                if (todoItem.completed!!) {
                    todoItem.completed = false
                }
                else {
                    todoItem.completed = true
                }

                todoItemList[position] = todoItem
                todoItemListAdapter.notifyDataSetChanged()
                editor_todo_list.smoothScrollToPosition(position)
            }

        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editor_todo_save.setOnClickListener(this)
        editor_todo_menu.setOnClickListener(this)
        editor_todo_back_arrow.setOnClickListener(this)
        editor_todo_add_task.setOnClickListener(this)

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
        todoItemListAdapter.changeTextColor(Color.parseColor(todo.textColor))

        editor_todo_title.setText(todo.title)
        noteColor(Color.parseColor(todo.color))
        textColor(Color.parseColor(todo.textColor))

        editor_todo_title.moveFocus()
    }

    override fun showAddTask(todoItem: TodoItem) {
        todoItemList.add(todoItem)
        todoItemListAdapter.updateAdapter()
    }

    override fun showUpdatedTask(position: Int, todoItem: TodoItem) {
        todoItemList[position] = todoItem
        todoItemListAdapter.updateAdapter()
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
        todoItemListAdapter.changeTextColor(textColor)
        editor_todo_title.setTextColor(textColor)
        editor_todo_back_arrow.setColorFilter(textColor)
        editor_todo_save.setColorFilter(textColor)
        editor_todo_add_task.setColorFilter(textColor)
        editor_todo_task_input.setTextColor(textColor)
        editor_todo_menu.setColorFilter(textColor)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.editor_todo_back_arrow -> activity!!.finish()

            R.id.editor_todo_menu -> showMenu()

            R.id.editor_todo_add_task -> addItem()

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

    private fun showAddItemDialog(title: String,
                                  actionTitle: String,
                                  message: String,
                                  listener: InputDialogListener) {
        val inputDialog = InputDialog.instance(title, actionTitle, message)
        inputDialog.setOnAddClickListener(listener)
        inputDialog.show(fragmentManager!!, INPUT_DIALOG_TAG)
    }

    private fun addItem() {
        val task = editor_todo_task_input.text.toString()
        presenter.addTask(task)
    }

    inner class TodoItemAdapter(var listener: TodoAdapterClickListener):
            RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {

        private var textColor: Int = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
            val view = layoutInflater.inflate(R.layout.item_row_edit_todo, parent, false)
            return TodoItemViewHolder(view, listener)
        }

        override fun getItemCount(): Int {
            return todoItemList.size
        }

        override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
            val todoItem = todoItemList[position]
            holder.bind(todoItem, textColor)
        }

        fun updateAdapter() {
            val adapterPosition = todoItemList.size - 1
            if (adapterPosition >= 0) {
                todoItemListAdapter.notifyDataSetChanged()
                editor_todo_list.smoothScrollToPosition(adapterPosition)
            }
        }

        fun changeTextColor(textColor: Int) {
            this.textColor = textColor
            todoItemListAdapter.updateAdapter()
        }

        inner class TodoItemViewHolder(itemView: View,
                                       private var todoListener: TodoAdapterClickListener) :
                RecyclerView.ViewHolder(itemView), View.OnClickListener {

            init {
                itemView.item_edit_todo_remove.setOnClickListener(this)
                itemView.item_edit_todo_edit.setOnClickListener(this)
                itemView.item_edit_todo_text.setOnClickListener(this)
            }

            override fun onClick(v: View?) {

                when (v?.id) {
                    itemView.item_edit_todo_remove.id ->
                        todoListener.onDeleteClick(adapterPosition)

                    itemView.item_edit_todo_edit.id ->
                        todoListener.onEditClick(adapterPosition)

                    itemView.item_edit_todo_text.id -> {
                        todoListener.onItemClick(adapterPosition)
                    }
                }
            }

            fun bind(todoItem: TodoItem, textColor: Int) {
                val position = adapterPosition + 1

                if (todoItem.completed!!) {
                    itemView.item_edit_todo_text.paintFlags = itemView.item_edit_todo_text.paintFlags or
                            Paint.STRIKE_THRU_TEXT_FLAG
                }
                else {
                    itemView.item_edit_todo_text.paintFlags = Paint.ANTI_ALIAS_FLAG
                }

                itemView.item_edit_todo_text.setTextColor(textColor)

                itemView.item_edit_todo_text.setTextColor(textColor)
                itemView.item_edit_todo_edit.setColorFilter(textColor)
                itemView.item_edit_todo_remove.setColorFilter(textColor)

                itemView.item_edit_todo_text.text = "$position.) " + todoItem.task
            }

        }

    }

}
