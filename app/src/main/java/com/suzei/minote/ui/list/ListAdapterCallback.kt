package com.suzei.minote.ui.list

import com.suzei.minote.data.local.entity.Notes

interface ListAdapterCallback<T> {

    fun onNotePasswordClick(note: Notes) {}

    fun onNoteClick(itemId: String)

    fun onNoteDeleted()

    fun scrollTo(position: Int)

    fun forceDelete(data: T)
}