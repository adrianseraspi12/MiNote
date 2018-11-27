package com.suzei.minote.data;

import java.util.List;

public interface DataSource {

    interface Listener<T> {

        void onDataAvailable(T result);

        void onDataUnavailable();

    }

    void getNote(int itemId, DataSource.Listener<Notes> listener);

    void getListOfNotes(DataSource.Listener<List<Notes>> listener);

    void deleteNote(Notes note);

}
