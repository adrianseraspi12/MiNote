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
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.list.ListActivity
import com.suzei.minote.ui.list.ListAdapterCallback
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.ui.list.ToastCallback
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.Turing
import com.suzei.minote.utils.dialogs.PasswordDialog
import com.suzei.minote.utils.recycler_view.decorator.LinearLayoutSpacing
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.custom_bottom_navigation.*
import kotlinx.android.synthetic.main.fragment_list.*

class ListNoteFragment : Fragment(), ListContract.View<Notes> {

    private lateinit var presenter: ListContract.Presenter<Notes>
    private lateinit var listAdapter: ListNoteAdapter
    private lateinit var listActivity: ListActivity

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
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mediumSpacing = resources.getDimension(R.dimen.margin_medium).toInt()
        list_notes.layoutManager = LinearLayoutManager(context)
        list_notes.addItemDecoration(LinearLayoutSpacing(mediumSpacing, mediumSpacing))
        list_notes.adapter = listAdapter
        list_tv_title.setText(R.string.notes)
    }

    override fun onStart() {
        super.onStart()
        LogMe.info("LOG ListNoteFragment = onStart()")
        presenter.setup()
    }

    override fun setPresenter(presenter: ListContract.Presenter<Notes>) {
        LogMe.info("LOG ListNoteFragment = setPresenter")
        this.presenter = presenter
    }

    override fun showListOfNotes(listOfNotes: MutableList<Notes>) {
        list_empty_placeholder.visibility = View.GONE
        listAdapter.update(listOfNotes)
        list_notes.smoothScrollToPosition(0)
    }

    override fun showListUnavailable() {
        list_empty_placeholder.visibility = View.VISIBLE
        list_iv_empty.setImageResource(R.drawable.ic_empty_notes)
        list_tv_empty_title.setText(R.string.no_notes_found_title)
        list_tv_empty_subtitle.setText(R.string.no_notes_found_subtitle)
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
            list_notes.scrollToPosition(position)
        }

        override fun forceDelete(data: Notes) {
            presenter.delete(data)
        }

    }

    private fun showEditor(itemId: String) {
        val intent = Intent(context, EditorNoteActivity::class.java)
        intent.putExtra(EditorNoteActivity.EXTRA_NOTE_ID, itemId)
        startActivity(intent)
    }

    private var toastCallback = object : ToastCallback {

        override fun onUndoClick() {
            listAdapter.retainDeletedItem()
            list_empty_placeholder.visibility = View.GONE
        }

        override fun onToastDismiss() {
            presenter.delete(listAdapter.tempDeletedNote!!)
        }

    }
}
