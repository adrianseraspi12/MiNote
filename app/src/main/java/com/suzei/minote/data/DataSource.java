package com.suzei.minote.data;

import java.util.List;

public interface DataSource {

    interface Listener {

        void onDataAvailable(List<Notes> listOfNotes);

        void onDataUnavailable();

    }

    void getListOfNotes(DataSource.Listener listener);

    void deleteNote(Notes note);

}
