package com.suzei.minote.ui.list.todo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.suzei.minote.R
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.ui.editor.todo.EditorTodoActivity
import com.suzei.minote.ui.list.ListActivity
import com.suzei.minote.ui.list.ListAdapterCallback
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.ui.list.ToastCallback
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.recycler_view.callback.ItemMoveTouchHelper
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.custom_bottom_navigation.*
import kotlinx.android.synthetic.main.fragment_list.*

class ListTodoFragment : Fragment(), ListContract.View<Todo> {

    companion object {

        const val TAG = "ListTodoFragment"

        internal fun newInstance(): ListTodoFragment {
            return ListTodoFragment()
        }
    }

    private lateinit var presenter: ListContract.Presenter<Todo>
    private lateinit var listActivity: ListActivity
    private lateinit var listTodoAdapter: ListTodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listActivity = activity as ListActivity
        listTodoAdapter = ListTodoAdapter(ArrayList(), listAdapterCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_tv_title.setText(R.string.todo)
        val itemMoveTouchHelper = ItemMoveTouchHelper()
        itemMoveTouchHelper.callback = itemTouchCallback

        val itemTouchHelper = ItemTouchHelper(itemMoveTouchHelper)
        itemTouchHelper.attachToRecyclerView(list_notes)

        list_notes.adapter = listTodoAdapter
        list_notes.layoutManager = LinearLayoutManager(context)
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

    override fun showListOfNotes(listOfNotes: MutableList<Todo>) {
        list_empty_placeholder.visibility = View.GONE
        listTodoAdapter.update(listOfNotes)
        list_notes.smoothScrollToPosition(0)
    }

    override fun showListUnavailable() {
        list_empty_placeholder.visibility = View.VISIBLE
        list_iv_empty.setImageResource(R.drawable.ic_empty_todo)
        list_tv_empty_title.setText(R.string.no_todo_found_title)
        list_tv_empty_subtitle.setText(R.string.no_todo_found_subtitle)
    }

    private var listAdapterCallback = object : ListAdapterCallback {

        override fun onNoteClick(itemId: String) {
            val intent = Intent(context, EditorTodoActivity::class.java)
            intent.putExtra(EditorTodoActivity.EXTRA_TODO_ID, itemId)
            startActivity(intent)
        }

    }

    private var itemTouchCallback = object : ItemMoveTouchHelper.MoveCallback {

        override fun isInDeleteArea(view: View): Boolean {
            val firstPosition = IntArray(2)
            val secondPosition = IntArray(2)
            list_delete_container.getLocationOnScreen(firstPosition)
            view.getLocationOnScreen(secondPosition)
            val l = firstPosition[1]
            val r = view.measuredHeight + secondPosition[1]
            return r > l
        }

        override fun removeItem(position: Int) {
            listTodoAdapter.removeTempItem(position)
            listActivity.showToastUndo(
                    getString(R.string.note_deleted),
                    toastCallback(position))
        }

        override fun onClearView() {
            val listActivity = activity as ListActivity
            list_delete_container.visibility = View.GONE
            listActivity.list_fab.visibility = View.VISIBLE
            listActivity.list_bottom_navigation_view.visibility = View.VISIBLE
        }

        override fun onDrag() {
            val listActivity = activity as ListActivity
            list_delete_container.visibility = View.VISIBLE
            listActivity.list_fab.visibility = View.GONE
            listActivity.list_bottom_navigation_view.visibility = View.GONE
        }

    }


    private fun toastCallback(position: Int) = object : ToastCallback {

        override fun onUndoClick() {
            listTodoAdapter.retainDeletedItem(position)
        }

        override fun onToastDismiss() {
            presenter.delete(listTodoAdapter.tempDeletedTodo!!)
            listTodoAdapter.forceRemove()
        }

    }
}
