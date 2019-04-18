package com.suzei.minote.ui.editor.todo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.dialogs.InputDialog
import com.suzei.minote.utils.dialogs.InputDialogListener
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

        for (singleTodo in todo.todoItems!!) {
            LogMe.info("TODO ITEM = ${singleTodo.task}")
        }

        this.todoItemList = todo.todoItems?.toMutableList() ?: ArrayList()
        todoItemListAdapter.notifyDataSetChanged()

        editor_todo_title.setText(todo.title)
    }

    override fun showAddTodoItem(todoItem: TodoItem) {
        print("TODO ITEM = ${todoItem.task})")
        todoItemList.add(todoItem)
        todoItemListAdapter.notifyDataSetChanged()
    }

    override fun showAddItemDialog() {
        val inputDialog = InputDialog.instance

        inputDialog.setOnAddClickListener(object: InputDialogListener {

            override fun onAddClick(message: String?) {

                if (message != null) {
                    print("TODO = $message")
                    presenter.addTask(message)
                }

            }

        })

        inputDialog.show(fragmentManager!!, INPUT_DIALOG_TAG)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.editor_todo_add_item -> {
                showAddItemDialog()
            }

            R.id.editor_todo_save -> {
                LogMe.info("Fragment =  Save")
                val title = editor_todo_title.text.toString()
                presenter.saveTodo(title, todoItemList)
            }

        }

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
            }

        }

    }

}
