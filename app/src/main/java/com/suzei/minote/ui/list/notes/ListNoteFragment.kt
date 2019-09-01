package com.suzei.minote.ui.list.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.list.ListContract
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.Turing
import com.suzei.minote.utils.dialogs.PasswordDialog
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.item_row_notes_default.view.*
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
        list_notes.layoutManager = LinearLayoutManager(context)
        list_notes.adapter = listAdapter
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun setPresenter(presenter: ListContract.Presenter<Notes>) {
        this.presenter = presenter
    }

    override fun showListOfNotes(listOfNotes: MutableList<Notes>) {
        list_empty_placeholder.visibility = View.GONE
        this.listOfNotes = listOfNotes
        listAdapter.notifyDataSetChanged()
    }

    override fun showListUnavailable() {
        list_empty_placeholder.visibility = View.VISIBLE
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

        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindNote(note: Notes) {
                itemView.item_notes_color.setBackgroundColor(Color.parseColor(note.color))
                itemView.item_notes_title.text = note.title

                itemView.item_notes_delete.setOnClickListener {

                    tempPosition = adapterPosition
                    tempNote = listOfNotes[tempPosition]

                    listOfNotes.remove(tempNote!!)
                    listAdapter.notifyItemRemoved(tempPosition)

                    presenter.checkSizeOfList(listOfNotes.size)
                    showSnackbar()

                }

                if (note.password != null) {
                    itemView.item_notes_password.visibility = View.VISIBLE
                    itemView.setOnClickListener { showPasswordDialog(note) }
                }
                else {
                    itemView.item_notes_password.visibility = View.GONE
                    itemView.setOnClickListener {
                        note.id?.let {
                            it1 -> presenter.showEditor(it1)
                        }
                    }
                }
            }

            private fun showPasswordDialog(note: Notes) {
                val decryptedPassword = note.password?.let { Turing.decrypt(it) }
                val passwordDialog = PasswordDialog.instance

                passwordDialog.setOnClosePasswordDialog(object: PasswordDialog.PasswordDialogListener {

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
                                }

                            }

                        })
                        .show()
            }

        }

    }

}
