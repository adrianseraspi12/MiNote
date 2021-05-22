package com.suzei.minote.ui.editor.todo

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.databinding.ItemRowEditTodoBinding
import com.suzei.minote.ext.setAlpha

class TodoSubtaskAdapter(var data: MutableList<TodoItem>,
                         private var onDeleteCallback: () -> Unit) : RecyclerView.Adapter<TodoSubtaskAdapter.ViewHolder>() {

    private var textColor: Int = 0xFFFFFF
    private var backgroundColor: Int = 0xFFFFFF

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRowEditTodoBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, SubTaskTextChangedListener())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.subTaskTextChangedListener.updatePosition(position)
        holder.binding.itemEditTodoRemove.setOnClickListener {
            holder.binding.itemEditTodoText.clearFocus()
            onDeleteCallback.invoke()
            delete(position)
        }

        holder.binding.itemRowEditTodoCvDone.setOnClickListener {
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
            val binding: ItemRowEditTodoBinding,
            var subTaskTextChangedListener: SubTaskTextChangedListener) : RecyclerView.ViewHolder(binding.root) {

        fun bind(todoItem: TodoItem) {
            val transparent = ContextCompat.getColor(itemView.context!!,
                    android.R.color.transparent)

            binding.itemEditTodoText.setTextColor(textColor)
            binding.itemEditTodoText.setHintTextColor(textColor.setAlpha(0.5f))
            binding.itemEditTodoRemove.setColorFilter(textColor)
            binding.itemRowEditTodoCvDone.strokeColor = textColor
            binding.itemRowEditTodoDivider.setBackgroundColor(textColor.setAlpha(0.5f))
            binding.itemRowEditTodoIvCheck.setColorFilter(backgroundColor)

            binding.itemEditTodoText.setText(todoItem.task)
            if (todoItem.completed == true) {
                binding.itemRowEditTodoCvDone.setCardBackgroundColor(textColor)
                binding.itemRowEditTodoIvCheck.visibility = View.VISIBLE
            } else {
                binding.itemRowEditTodoCvDone.setCardBackgroundColor(transparent)
                binding.itemRowEditTodoIvCheck.visibility = View.GONE
            }
        }

        fun enableTextWatcher() {
            binding.itemEditTodoText.addTextChangedListener(subTaskTextChangedListener)
        }

        fun disableTextWatcher() {
            binding.itemEditTodoText.removeTextChangedListener(subTaskTextChangedListener)
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