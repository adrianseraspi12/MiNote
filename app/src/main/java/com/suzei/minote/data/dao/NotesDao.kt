package com.suzei.minote.data.dao

import com.suzei.minote.data.entity.Notes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotesDao {

    @Query("SELECT * FROM NOTES")
    fun findAllNotes(): List<Notes>

    @Query("SELECT * FROM NOTES WHERE _id=:itemId")
    fun findNoteById(itemId: Int): Notes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveNote(note: Notes)

    @Delete
    fun deleteNote(note: Notes)

}
