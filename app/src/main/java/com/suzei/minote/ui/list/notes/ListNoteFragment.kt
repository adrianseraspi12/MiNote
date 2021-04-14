package com.suzei.minote.ui.list.notes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.databinding.FragmentListBinding
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.list.ListActivity
import com.suzei.minote.ui.list.ListAdapterCallback
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.ui.list.ToastCallback
import com.suzei.minote.ui.settings.SettingsActivity
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.Turing
import com.suzei.minote.utils.dialogs.PasswordDialog
import com.suzei.minote.utils.recycler_view.decorator.LinearLayoutSpacing

class ListNoteFragment : Fragment(), ListContract.View<Notes> {

    private lateinit var presenter: ListContract.Presenter<Notes>
    private lateinit var listAdapter: ListNoteAdapter
    private lateinit var listActivity: ListActivity
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    companion object {

        const val TAG = "ListNoteFragment"

        internal fun newInstance(): ListNoteFragment {
            return ListNoteFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogMe.info("LOG ListNoteFragment = onCreate()")
        listActivity = activity as ListActivity
        listAdapter = ListNoteAdapter(ArrayList(), listAdapterCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listTvTitle.setText(R.string.notes)
        setupRecyclerView()
        setupSettingsButton()
    }

    override fun onStart() {
        super.onStart()
        presenter.setup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setPresenter(presenter: ListContract.Presenter<Notes>) {
        this.presenter = presenter
    }

    override fun showListOfNotes(listOfNotes: MutableList<Notes>) {
        binding.listEmptyPlaceholder.visibility = View.GONE
        listAdapter.update(listOfNotes)
        binding.listNotes.smoothScrollToPosition(0)
    }

    override fun showListUnavailable() {
        binding.listEmptyPlaceholder.visibility = View.VISIBLE
        binding.listIvEmpty.setImageResource(R.drawable.ic_empty_notes)
        binding.listTvEmptyTitle.setText(R.string.no_notes_found_title)
        binding.listTvEmptySubtitle.setText(R.string.no_notes_found_subtitle)
    }

    private fun showEditor(itemId: String) {
        val intent = Intent(context, EditorNoteActivity::class.java)
        intent.putExtra(EditorNoteActivity.EXTRA_NOTE_ID, itemId)
        startActivity(intent)
    }

    private fun setupSettingsButton() {
        binding.listBtnSettings.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val mediumSpacing = resources.getDimension(R.dimen.margin_medium).toInt()
        binding.listNotes.layoutManager = LinearLayoutManager(context)
        binding.listNotes.addItemDecoration(LinearLayoutSpacing(mediumSpacing, mediumSpacing))
        binding.listNotes.adapter = listAdapter
    }

    private var toastCallback = object : ToastCallback {

        override fun onUndoClick() {
            listAdapter.retainDeletedItem()
            binding.listEmptyPlaceholder.visibility = View.GONE
        }

        override fun onToastDismiss() {
            presenter.delete(listAdapter.tempDeletedNote!!)
        }

    }

    private var listAdapterCallback = object : ListAdapterCallback<Notes> {

        override fun onNotePasswordClick(note: Notes) {
            val decryptedPassword = note.password?.let { Turing.decrypt(it) } ?: ""
            val passwordDialog = PasswordDialog.instance(decryptedPassword) { password ->
                if (password.isEmpty()) return@instance
                note.id?.let { noteId ->
                    showEditor(noteId)
                }
            }
            passwordDialog.show(fragmentManager!!, "PasswordDialog")
        }

        override fun onNoteClick(itemId: String) {
            showEditor(itemId)
        }

        override fun onNoteDeleted() {
            listActivity.showToastUndo(
                    getString(R.string.note_deleted),
                    toastCallback)
            if (listAdapter.itemCount == 0) {
                showListUnavailable()
            }
        }

        override fun scrollTo(position: Int) {
            binding.listNotes.scrollToPosition(position)
        }

        override fun forceDelete(data: Notes) {
            presenter.delete(data)
        }

    }
}
