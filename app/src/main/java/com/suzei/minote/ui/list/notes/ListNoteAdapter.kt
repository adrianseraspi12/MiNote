package com.suzei.minote.ui.list.notes

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.ui.list.ListAdapterCallback
import kotlinx.android.synthetic.main.item_row_notes.view.*
import org.threeten.bp.format.DateTimeFormatter

class ListNoteAdapter(
        private var listOfNotes: MutableList<Notes>,
        private var listAdapterCallback: ListAdapterCallback<Notes>
) : RecyclerView.Adapter<ListNoteAdapter.ViewHolder>() {

    var tempDeletedNote: Notes? = null
    var tempDeletedPos: Int = -1

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
        if (tempDeletedPos != -1) {
            //  Remove old temporary note
            listAdapterCallback.forceDelete(tempDeletedNote!!)
            forceRemove()
        }
        val note = listOfNotes[position]
        tempDeletedNote = note
        tempDeletedPos = position
        listOfNotes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listOfNotes.size)
    }

    fun retainDeletedItem() {
        listOfNotes.add(tempDeletedPos, tempDeletedNote!!)
        notifyItemInserted(tempDeletedPos)
        if (tempDeletedPos == 0) {
            notifyItemChanged(tempDeletedPos + 1)
        }
        listAdapterCallback.scrollTo(tempDeletedPos)
        tempDeletedNote = null
        tempDeletedPos = -1
    }

    fun forceRemove() {
        tempDeletedNote = null
        tempDeletedPos = -1
    }

    inner class ViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindNote(note: Notes) {
            val datetimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

            itemView.item_notes_color.setCardBackgroundColor(Color.parseColor(note.color))
            itemView.item_notes_title.text = note.title
            itemView.item_notes_subtitle.text = note.createdDate?.format(datetimeFormatter)
            itemView.row_notes_delete.setOnClickListener {
                removeTempItem(adapterPosition)
                listAdapterCallback.onNoteDeleted()
            }

            if (note.password != null) {
                itemView.item_notes_content.text = context.getString(R.string.locked)
                itemView.item_rootview.setOnClickListener {
                    listAdapterCallback.onNotePasswordClick(note)
                }
            } else {
                itemView.item_notes_content.text = note.message
                itemView.item_rootview.setOnClickListener {
                    note.id?.let { itemId ->
                        listAdapterCallback.onNoteClick(itemId)
                    }
                }
            }
        }
    }
}