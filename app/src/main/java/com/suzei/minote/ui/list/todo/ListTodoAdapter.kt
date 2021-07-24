package com.suzei.minote.ui.list.todo

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.databinding.ItemRowTodoBinding
import com.suzei.minote.ui.list.ListAdapterCallback
import org.threeten.bp.format.DateTimeFormatter

class ListTodoAdapter(var data: MutableList<Todo>,
                      private var listAdapterCallback: ListAdapterCallback<Todo>) : RecyclerView.Adapter<ListTodoAdapter.ViewHolder>() {

    var tempDeletedTodo: Todo? = null
    var tempDeletedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ItemRowTodoBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun update(listOfNotes: MutableList<Todo>) {
        this.data = listOfNotes
        notifyDataSetChanged()
    }

    fun add(note: Todo, position: Int) {
        data.add(position, note)
        notifyItemInserted(position)
    }

    fun removeTempItem(position: Int) {
        if (tempDeletedPos != -1) {
            //  Remove old temporary to do
            listAdapterCallback.forceDelete(tempDeletedTodo!!)
            forceRemove()
        }
        val note = data[position]
        tempDeletedTodo = note
        tempDeletedPos = position
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }

    fun retainDeletedItem() {
        data.add(tempDeletedPos, tempDeletedTodo!!)
        notifyItemInserted(tempDeletedPos)
        if (tempDeletedPos == 0) {
            notifyItemChanged(tempDeletedPos + 1)
        }
        listAdapterCallback.scrollTo(tempDeletedPos)
        tempDeletedTodo = null
        tempDeletedPos = -1
    }

    fun forceRemove() {
        tempDeletedTodo = null
    }

    inner class ViewHolder(private val binding: ItemRowTodoBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(todo: Todo) {
            val datetimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
            val subtaskCount = todo.todoItems?.size ?: 0

            binding.itemTodoColor.setCardBackgroundColor(Color.parseColor(todo.color))
            binding.itemTodoTitle.text = todo.title
            binding.itemTodoSubtitle.text = todo.createdDate?.format(datetimeFormatter)
            binding.rowNotesDelete.setOnClickListener {
                removeTempItem(adapterPosition)
                listAdapterCallback.onNoteDeleted()
            }

            if (subtaskCount < 2) {
                binding.itemTodoContent.text = "$subtaskCount Item"
            } else {
                binding.itemTodoContent.text = "$subtaskCount Items"
            }

            binding.itemRootview.setOnClickListener {
                todo.id?.let { itemId ->
                    listAdapterCallback.onNoteClick(itemId)
                }
            }
        }

    }

}