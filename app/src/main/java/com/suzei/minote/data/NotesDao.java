package com.suzei.minote.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

@Dao
public interface NotesDao {

    @Query("SELECT * FROM NOTES")
    List<Notes> findAllNotes();

    @Delete
    void deleteNote(Notes note);

}
