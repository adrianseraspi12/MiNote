package com.suzei.minote.ui.list.todo

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.ui.list.ListAdapterCallback
import kotlinx.android.synthetic.main.item_row_todo.view.*
import org.threeten.bp.format.DateTimeFormatter

class ListTodoAdapter(var data: MutableList<Todo>,
                      private var listAdapterCallback: ListAdapterCallback) : RecyclerView.Adapter<ListTodoAdapter.ViewHolder>() {

    var tempDeletedTodo: Todo? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_todo, parent, false)
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
        val note = data[position]
        tempDeletedTodo = note
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }

    fun retainDeletedItem(position: Int) {
        data.add(position, tempDeletedTodo!!)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
        tempDeletedTodo = null
    }

    fun forceRemove() {
        tempDeletedTodo = null
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(todo: Todo) {
            val datetimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
            val subtaskCount = todo.todoItems?.size ?: 0

            itemView.item_todo_color.setCardBackgroundColor(Color.parseColor(todo.color))
            itemView.item_todo_title.text = todo.title
            itemView.item_todo_subtitle.text = todo.createdDate?.format(datetimeFormatter)

            if (subtaskCount < 2) {
                itemView.item_todo_content.text = "$subtaskCount Item"
            } else {
                itemView.item_todo_content.text = "$subtaskCount Items"
            }

            itemView.setOnClickListener {
                todo.id?.let { itemId ->
                    listAdapterCallback.onNoteClick(itemId)
                }
            }
        }

    }

}