package com.suzei.minote.ui.list.notes

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import kotlinx.android.synthetic.main.item_row_notes.view.*
import org.threeten.bp.format.DateTimeFormatter

class ListNoteAdapter(
        private var listOfNotes: MutableList<Notes>,
        private var listAdapterCallback: ListAdapterCallback
) : RecyclerView.Adapter<ListNoteAdapter.ViewHolder>() {

    var tempDeletedNote: Notes? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(
                R.layout.item_row_notes,
                parent,
                false)
        return ViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = listOfNotes[position]
        holder.bindNote(note)
    }

    override fun getItemCount(): Int {
        return listOfNotes.size
    }

    fun update(listOfNotes: MutableList<Notes>) {
        this.listOfNotes = listOfNotes
        notifyDataSetChanged()
    }

    fun add(note: Notes, position: Int) {
        listOfNotes.add(position, note)
        notifyItemInserted(position)
    }

    fun removeTempItem(position: Int) {
        val note = listOfNotes[position]
        tempDeletedNote = note
        listOfNotes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listOfNotes.size)
    }

    fun retainDeletedItem(position: Int) {
        listOfNotes.add(position, tempDeletedNote!!)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listOfNotes.size)
        tempDeletedNote = null
    }

    fun forceRemove() {
        tempDeletedNote = null
    }

    inner class ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindNote(note: Notes) {
            val datetimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

            itemView.item_notes_color.setCardBackgroundColor(Color.parseColor(note.color))
            itemView.item_notes_title.text = note.title
            itemView.item_notes_subtitle.text = note.createdDate?.format(datetimeFormatter)

            if (note.password != null) {
                itemView.item_notes_content.text = context.getString(R.string.locked)
                itemView.setOnClickListener {
                    listAdapterCallback.onNotePasswordClick(note)
                }
            } else {
                itemView.item_notes_content.text = note.message
                itemView.setOnClickListener {
                    note.id?.let { itemId ->
                        listAdapterCallback.onNoteClick(itemId)
                    }
                }
            }
        }

    }

}

interface ListAdapterCallback {

    fun onNotePasswordClick(note: Notes)

    fun onNoteClick(itemId: String)

}