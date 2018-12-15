package com.suzei.minote.data;

import com.suzei.minote.data.entity.Notes;

import java.util.List;

public interface DataSource {

    interface NoteListener {

        void onDataAvailable(Notes note);

        void onDataUnavailable();

    }

    interface ListNoteListener {

        void onDataAvailable(List<Notes> listOfNote);

        void onDataUnavailable();

    }

    void saveNote(Notes note);

    void getNote(int itemId, NoteListener listener);

    void getListOfNotes(ListNoteListener listener);

    void deleteNote(Notes note);

}
