package com.suzei.minote.data;

import java.util.List;

import androidx.room.Query;

public interface NotesDao {

    @Query("SELECT * FROM NOTES")
    List<Notes> findAllNotes();

}
