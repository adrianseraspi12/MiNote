package com.suzei.minote.ui.list.todo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.suzei.minote.R
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.databinding.FragmentListBinding
import com.suzei.minote.ui.editor.todo.EditorTodoActivity
import com.suzei.minote.ui.list.ListActivity
import com.suzei.minote.ui.list.ListAdapterCallback
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.ui.list.ToastCallback
import com.suzei.minote.ui.settings.SettingsActivity
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.recycler_view.decorator.LinearLayoutSpacing

class ListTodoFragment : Fragment(), ListContract.View<Todo> {

    private lateinit var presenter: ListContract.Presenter<Todo>
    private lateinit var listActivity: ListActivity
    private lateinit var listTodoAdapter: ListTodoAdapter
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    companion object {

        const val TAG = "ListTodoFragment"

        internal fun newInstance(): ListTodoFragment {
            return ListTodoFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listActivity = activity as ListActivity
        listTodoAdapter = ListTodoAdapter(ArrayList(), listAdapterCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listTvTitle.setText(R.string.todo)
        setupRecyclerView()
        setupSettingsButton()
        presenter.setup()
    }

    override fun onStart() {
        super.onStart()
        presenter.setup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setPresenter(presenter: ListContract.Presenter<Todo>) {
        LogMe.info("LOG ListTodoFragment = setPresenter()")
        this.presenter = presenter
    }

    override fun showListOfNotes(listOfNotes: MutableList<Todo>) {
        binding.listEmptyPlaceholder.visibility = View.GONE
        listTodoAdapter.update(listOfNotes)
        binding.listNotes.smoothScrollToPosition(0)
    }

    override fun showListUnavailable() {
        binding.listEmptyPlaceholder.visibility = View.VISIBLE
        binding.listIvEmpty.setImageResource(R.drawable.ic_empty_todo)
        binding.listTvEmptyTitle.setText(R.string.no_todo_found_title)
        binding.listTvEmptySubtitle.setText(R.string.no_todo_found_subtitle)
    }

    private fun setupSettingsButton() {
        binding.listBtnSettings.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val mediumSpacing = resources.getDimension(R.dimen.margin_medium).toInt()
        binding.listNotes.addItemDecoration(LinearLayoutSpacing(mediumSpacing, mediumSpacing))
        binding.listNotes.adapter = listTodoAdapter
        binding.listNotes.layoutManager = LinearLayoutManager(context)
    }

    private var toastCallback = object : ToastCallback {

        override fun onUndoClick() {
            listTodoAdapter.retainDeletedItem()
            binding.listEmptyPlaceholder.visibility = View.GONE
        }

        override fun onToastDismiss() {
            presenter.delete(listTodoAdapter.tempDeletedTodo!!)
        }
    }

    private var listAdapterCallback = object : ListAdapterCallback<Todo> {

        override fun onNoteClick(itemId: String) {
            val intent = Intent(context, EditorTodoActivity::class.java)
            intent.putExtra(EditorTodoActivity.EXTRA_TODO_ID, itemId)
            startActivity(intent)
        }

        override fun onNoteDeleted() {
            listActivity.showToastUndo(
                    getString(R.string.note_deleted),
                    toastCallback)
            if (listTodoAdapter.itemCount == 0) {
                showListUnavailable()
            }
        }

        override fun scrollTo(position: Int) {
            binding.listNotes.scrollToPosition(position)
        }

        override fun forceDelete(data: Todo) {
            presenter.delete(data)
        }

    }
}