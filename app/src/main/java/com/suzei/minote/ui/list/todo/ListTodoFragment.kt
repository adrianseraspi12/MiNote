package com.suzei.minote.ui.list.todo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.suzei.minote.R
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.ui.editor.todo.EditorTodoActivity
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.item_row_notes_default.view.*

class ListTodoFragment : Fragment(), ListContract.View<Todo> {

    companion object {

        internal fun newInstance(): ListTodoFragment {
            return ListTodoFragment()
        }
    }

    private lateinit var presenter: ListContract.Presenter<Todo>

    private lateinit var listOfTodo: MutableList<Todo>

    private lateinit var adapter: ListAdapter

    private var tempTodo: Todo? = null
    private var consecutiveTodo: Todo? = null
    private var tempPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ListAdapter()
        listOfTodo = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_notes.layoutManager = LinearLayoutManager(context)
        list_notes.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        LogMe.info("LOG ListTodoFragment = onStart()")

        presenter.start()
    }

    override fun onDestroyView() {
        LogMe.info("LOG ListTodoFragment = onDestroyView()")
        super.onDestroyView()
    }

    override fun setPresenter(presenter: ListContract.Presenter<Todo>) {
        LogMe.info("LOG ListTodoFragment = setPresenter()")

        this.presenter = presenter
    }

    override fun showListOfNotes(listOfTodo: MutableList<Todo>) {
        list_empty_placeholder.visibility = View.GONE
        this.listOfTodo = listOfTodo
        adapter.notifyDataSetChanged()
    }

    override fun showListUnavailable() {
        list_empty_placeholder.visibility = View.VISIBLE
    }

    override fun insertNoteToList(data: Todo, position: Int) {
        listOfTodo.add(position, data)
        adapter.notifyItemInserted(position)
    }

    override fun redirectToEditorActivity(itemId: String) {
        val intent = Intent(context, EditorTodoActivity::class.java)
        intent.putExtra(EditorTodoActivity.EXTRA_TODO_ID, itemId)
        startActivity(intent)
    }

    inner class ListAdapter: RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
            val view = layoutInflater.inflate(R.layout.item_row_notes_default, parent, false)
            return ListViewHolder(view)
        }

        override fun getItemCount(): Int {
            return listOfTodo.size
        }

        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            val todo = listOfTodo[position]
            holder.bind(todo)
        }

        inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            fun bind(todo: Todo) {
                itemView.item_notes_color.setBackgroundColor(Color.parseColor(todo.color))
                itemView.item_notes_title.text = todo.title
                itemView.setOnClickListener {
                    todo.id?.let {
                        presenter.showEditor(it)
                    }
                }

                itemView.item_notes_delete.setOnClickListener {
                    tempPosition = adapterPosition
                    tempTodo = listOfTodo[tempPosition]

                    listOfTodo.remove(tempTodo!!)
                    adapter.notifyItemRemoved(tempPosition)

                    presenter.checkSizeOfList(listOfTodo.size)
                    showSnackbar()
                }
            }

            private fun showSnackbar() {
                Snackbar.make(list_root, "Todo Deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo") { insertNoteToList(tempTodo!!, tempPosition) }
                        .addCallback(object : Snackbar.Callback() {

                            override fun onShown(sb: Snackbar?) {
                                super.onShown(sb)
                                consecutiveTodo = tempTodo
                            }

                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                when (event) {

                                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE ->
                                        presenter.delete(consecutiveTodo!!)

                                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> {
                                        presenter.delete(tempTodo!!)
                                        consecutiveTodo = null
                                        tempTodo = null
                                        tempPosition = -1
                                    }
                                }

                            }

                        })
                        .show()
            }

        }

    }

}
