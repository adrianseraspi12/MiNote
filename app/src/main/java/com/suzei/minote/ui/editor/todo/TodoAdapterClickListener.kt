package com.suzei.minote.ui.editor.todo

interface TodoAdapterClickListener {

    fun onEditClick(position: Int)
    fun onDeleteClick(position: Int)
    fun onItemClick(position: Int)

}