package com.suzei.minote.data.local.dao

import androidx.room.*
import com.suzei.minote.data.local.entity.Notes

@Dao
interface NotesDao {

    @Query("SELECT * FROM NOTES")
    fun findAllNotes(): List<Notes>

    @Query("SELECT * FROM NOTES WHERE _id=:itemId")
    fun findNoteById(itemId: String): Notes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveNote(note: Notes)

    @Update
    fun updateNote(note: Notes)

    @Delete
    fun deleteNote(note: Notes)

}
