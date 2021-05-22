package com.suzei.minote.ui.editor.todo

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.R
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.ext.setAlpha
import kotlinx.android.synthetic.main.item_row_edit_todo.view.*

class TodoSubtaskAdapter(var data: MutableList<TodoItem>,
                         private var onDeleteCallback: () -> Unit) : RecyclerView.Adapter<TodoSubtaskAdapter.ViewHolder>() {

    private var textColor: Int = 0xFFFFFF
    private var backgroundColor: Int = 0xFFFFFF

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_row_edit_todo, parent, false)
        return ViewHolder(view, SubTaskTextChangedListener())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.subTaskTextChangedListener.updatePosition(position)
        holder.itemView.item_edit_todo_remove.setOnClickListener {
            holder.itemView.item_edit_todo_text.clearFocus()
            onDeleteCallback.invoke()
            delete(position)
        }

        holder.itemView.item_row_edit_todo_cv_done.setOnClickListener {
            val isDone = item.completed ?: false
            isTaskCompleted(position, !isDone)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.enableTextWatcher()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.disableTextWatcher()
    }

    fun add(todoItem: TodoItem) {
        data.add(todoItem)
        notifyDataSetChanged()
    }

    fun changeTextColor(color: Int) {
        this.textColor = color
        notifyDataSetChanged()
    }

    fun changeBackgroundColor(color: Int) {
        this.backgroundColor = color
        notifyDataSetChanged()
    }

    fun updateData(listOfTask: MutableList<TodoItem>) {
        this.data = listOfTask
        notifyDataSetChanged()
    }

    private fun isTaskCompleted(position: Int, isDone: Boolean) {
        data[position].completed = isDone
        notifyItemChanged(position)
    }

    private fun delete(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }

    inner class ViewHolder(
            itemView: View,
            var subTaskTextChangedListener: SubTaskTextChangedListener) : RecyclerView.ViewHolder(itemView) {

        fun bind(todoItem: TodoItem) {
            val transparent = ContextCompat.getColor(itemView.context!!,
                    android.R.color.transparent)
            itemView.item_edit_todo_text.setTextColor(textColor)
            itemView.item_edit_todo_text.setHintTextColor(textColor.setAlpha(0.5f))
            itemView.item_edit_todo_remove.setColorFilter(textColor)
            itemView.item_row_edit_todo_cv_done.strokeColor = textColor
            itemView.item_row_edit_todo_divider.setBackgroundColor(textColor.setAlpha(0.5f))
            itemView.item_row_edit_todo_iv_check.setColorFilter(backgroundColor)

            itemView.item_edit_todo_text.setText(todoItem.task)
            if (todoItem.completed == true) {
                itemView.item_row_edit_todo_cv_done.setCardBackgroundColor(textColor)
                itemView.item_row_edit_todo_iv_check.visibility = View.VISIBLE
            } else {
                itemView.item_row_edit_todo_cv_done.setCardBackgroundColor(transparent)
                itemView.item_row_edit_todo_iv_check.visibility = View.GONE
            }
        }

        fun enableTextWatcher() {
            itemView.item_edit_todo_text.addTextChangedListener(subTaskTextChangedListener)
        }

        fun disableTextWatcher() {
            itemView.item_edit_todo_text.removeTextChangedListener(subTaskTextChangedListener)
        }

    }

    inner class SubTaskTextChangedListener : TextWatcher {

        private var position = -1

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val charSequence = p0 ?: ""
            if (charSequence.isNotEmpty() && position != -1) {
                val todoItem = data[position]
                todoItem.task = charSequence.toString()
                data[position] = todoItem
            }
        }

        fun updatePosition(position: Int) {
            this.position = position
        }
    }
}