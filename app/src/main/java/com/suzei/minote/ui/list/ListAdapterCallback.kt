package com.suzei.minote.ui.list

import com.suzei.minote.data.entity.Notes

interface ListAdapterCallback {

    fun onNotePasswordClick(note: Notes)

    fun onNoteClick(itemId: String)

}