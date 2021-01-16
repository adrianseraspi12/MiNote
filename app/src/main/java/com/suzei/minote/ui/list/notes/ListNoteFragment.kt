package com.suzei.minote.ui.list.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.list.ListActivity
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.Turing
import com.suzei.minote.utils.dialogs.PasswordDialog
import com.suzei.minote.utils.recycler_view.callback.ItemMoveTouchHelper
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.custom_bottom_navigation.*
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.item_row_notes_default.view.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class ListNoteFragment : Fragment(), ListContract.View<Notes> {

    private lateinit var presenter: ListContract.Presenter<Notes>

    private lateinit var listOfNotes: MutableList<Notes>

    private lateinit var listAdapter: ListAdapter

    private var tempNote: Notes? = null
    private var consecutiveNote: Notes? = null
    private var tempPosition: Int = 0

    companion object {

        internal fun newInstance(): ListNoteFragment {
            return ListNoteFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listOfNotes = ArrayList()
        listAdapter = ListAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemMoveTouchHelper = ItemMoveTouchHelper()
        itemMoveTouchHelper.callback = object : ItemMoveTouchHelper.MoveCallback {

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
                listAdapter.removeItem(position)
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
        this.listOfNotes = listOfNotes
        listAdapter.notifyDataSetChanged()
        list_notes.smoothScrollToPosition(0)
    }

    override fun showListUnavailable() {
        list_empty_placeholder.visibility = View.VISIBLE
        list_iv_empty.setImageResource(R.drawable.ic_empty_notes)
        list_tv_empty_title.setText(R.string.no_notes_found_title)
        list_tv_empty_subtitle.setText(R.string.no_notes_found_subtitle)
    }

    override fun insertNoteToList(note: Notes, position: Int) {
        list_empty_placeholder.visibility = View.GONE
        listOfNotes.add(position, note)
        listAdapter.notifyItemInserted(position)
    }

    override fun redirectToEditorActivity(itemId: String) {
        val intent = Intent(context, EditorNoteActivity::class.java)
        intent.putExtra(EditorNoteActivity.EXTRA_NOTE_ID, itemId)
        startActivity(intent)
    }

    inner class ListAdapter : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
            val view = layoutInflater.inflate(
                    R.layout.item_row_notes_default,
                    parent,
                    false)

            return ListViewHolder(view)
        }

        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            val note = listOfNotes[position]
            holder.bindNote(note)
        }

        override fun getItemCount(): Int {
            return listOfNotes.size
        }

        fun removeItem(position: Int) {
            listOfNotes.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listOfNotes.size)
        }

        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindNote(note: Notes) {
                val datetimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

                itemView.item_notes_color.setCardBackgroundColor(Color.parseColor(note.color))
                itemView.item_notes_title.text = note.title
                itemView.item_notes_subtitle.text = note.createdDate?.format(datetimeFormatter)

                if (note.password != null) {
                    itemView.item_notes_content.text = getString(R.string.locked)
                    itemView.setOnClickListener { showPasswordDialog(note) }
                } else {
                    itemView.item_notes_content.text = note.message
                    itemView.setOnClickListener {
                        note.id?.let { it1 ->
                            presenter.showEditor(it1)
                        }
                    }
                }
            }

            private fun showPasswordDialog(note: Notes) {
                val decryptedPassword = note.password?.let { Turing.decrypt(it) }
                val passwordDialog = PasswordDialog.instance()

                passwordDialog.setOnClosePasswordDialog(object : PasswordDialog.PasswordDialogListener {

                    override fun onClose(password: String) {
                        if (decryptedPassword != password) {
                            Toast.makeText(context,
                                    "Wrong Password, Please Try again",
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            note.id?.let {
                                presenter.showEditor(it)
                            }
                        }
                    }

                })

                passwordDialog.show(fragmentManager!!, "PasswordDialog")
            }

            private fun showSnackbar() {
                Snackbar.make(list_root, "Note Deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo") { insertNoteToList(tempNote!!, tempPosition) }
                        .addCallback(object : Snackbar.Callback() {

                            override fun onShown(sb: Snackbar?) {
                                super.onShown(sb)
                                consecutiveNote = tempNote
                            }

                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                when (event) {

                                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE -> {
                                        presenter.delete(consecutiveNote!!)
                                    }

                                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> {
                                        presenter.delete(tempNote!!)
                                        consecutiveNote = null
                                        tempNote = null
                                        tempPosition = -1
                                    }
                                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION -> {}
                                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_MANUAL -> {}
                                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE -> {}
                                }

                            }

                        })
                        .show()
            }

        }

    }

}
