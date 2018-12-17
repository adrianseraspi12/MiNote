package com.suzei.minote.data.dao;

import com.suzei.minote.data.entity.Notes;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface NotesDao {

    @Query("SELECT * FROM NOTES")
    List<Notes> findAllNotes();

    @Query("SELECT * FROM NOTES WHERE _id=:itemId")
    Notes findNoteById(int itemId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveNote(Notes note);

    @Delete
    void deleteNote(Notes note);

}