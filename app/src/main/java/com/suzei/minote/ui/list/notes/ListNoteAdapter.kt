package com.suzei.minote.ui.list.notes

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.databinding.ItemRowNotesBinding
import com.suzei.minote.ui.list.ListAdapterCallback
import org.threeten.bp.format.DateTimeFormatter

class ListNoteAdapter(
        private var listOfNotes: MutableList<Notes>,
        private var listAdapterCallback: ListAdapterCallback<Notes>
) : RecyclerView.Adapter<ListNoteAdapter.ViewHolder>() {

    var tempDeletedNote: Notes? = null
    var tempDeletedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding = ItemRowNotesBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(context, binding)
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

    inner class ViewHolder(val context: Context, val binding: ItemRowNotesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindNote(note: Notes) {
            val datetimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

            binding.itemNotesColor.setCardBackgroundColor(Color.parseColor(note.color))
            binding.itemNotesTitle.text = note.title
            binding.itemNotesSubtitle.text = note.createdDate?.format(datetimeFormatter)
            binding.rowNotesDelete.setOnClickListener {
                removeTempItem(adapterPosition)
                listAdapterCallback.onNoteDeleted()
            }

            if (note.password != null) {
                binding.itemNotesContent.text = context.getString(R.string.locked)
                binding.itemRootview.setOnClickListener {
                    listAdapterCallback.onNotePasswordClick(note)
                }
            } else {
                binding.itemNotesContent.text = note.message
                binding.itemRootview.setOnClickListener {
                    note.id?.let { itemId ->
                        listAdapterCallback.onNoteClick(itemId)
                    }
                }
            }
        }
    }
}