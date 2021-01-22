package com.suzei.minote.ui.list.notes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.list.ListActivity
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.ui.list.ToastCallback
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.Turing
import com.suzei.minote.utils.dialogs.PasswordDialog
import com.suzei.minote.utils.recycler_view.callback.ItemMoveTouchHelper
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.custom_bottom_navigation.*
import kotlinx.android.synthetic.main.fragment_list.*

class ListNoteFragment : Fragment(), ListContract.View<Notes> {

    private lateinit var presenter: ListContract.Presenter<Notes>
    private lateinit var listAdapter: ListNoteAdapter
    private lateinit var listActivity: ListActivity

    companion object {

        internal fun newInstance(): ListNoteFragment {
            return ListNoteFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listActivity = activity as ListActivity
        listAdapter = ListNoteAdapter(ArrayList(), listAdapterCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemMoveTouchHelper = ItemMoveTouchHelper()
        itemMoveTouchHelper.callback = itemTouchCallback

        val itemTouchHelper = ItemTouchHelper(itemMoveTouchHelper)
        itemTouchHelper.attachToRecyclerView(list_notes)

        list_notes.layoutManager = LinearLayoutManager(context)
        list_notes.adapter = listAdapter
        list_tv_title.setText(R.string.notes)
    }

    override fun onStart() {
        super.onStart()
        LogMe.info("LOG ListNoteFragment = onStart()")
        presenter.start()
    }

    override fun onDestroyView() {
        LogMe.info("LOG ListNoteFragment = onDestroyView()")
        super.onDestroyView()
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

    override fun insertNoteToList(data: Notes, position: Int) {
        list_empty_placeholder.visibility = View.GONE
        listAdapter.add(data, position)
    }

    override fun redirectToEditorActivity(itemId: String) {
        val intent = Intent(context, EditorNoteActivity::class.java)
        intent.putExtra(EditorNoteActivity.EXTRA_NOTE_ID, itemId)
        startActivity(intent)
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
            listAdapter.removeTempItem(position)
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

    private var listAdapterCallback = object : ListAdapterCallback {

        override fun onNotePasswordClick(note: Notes) {
            val decryptedPassword = note.password?.let { Turing.decrypt(it) }
            val passwordDialog = PasswordDialog.instance {
                if (decryptedPassword != it) {
                    Toast.makeText(context,
                            "Wrong Password, Please Try again",
                            Toast.LENGTH_SHORT).show()
                } else {
                    note.id?.let {
                        presenter.showEditor(it)
                    }
                }
            }
            passwordDialog.show(fragmentManager!!, "PasswordDialog")
        }

        override fun onNoteClick(itemId: String) {
            presenter.showEditor(itemId)
        }

    }

    private fun toastCallback(position: Int) = object : ToastCallback {

        override fun onUndoClick() {
            listAdapter.retainDeletedItem(position)
        }

        override fun onToastDismiss() {
            presenter.delete(listAdapter.tempDeletedNote!!)
            listAdapter.forceRemove()
        }

    }
}
