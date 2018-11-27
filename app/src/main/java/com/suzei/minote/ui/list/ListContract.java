package com.suzei.minote.ui.list;


import com.suzei.minote.data.Notes;

import java.util.List;

public interface ListContract {

    interface View {

        void setPresenter(Presenter presenter);

        void showListOfNotes(List<Notes> listOfNotes);

        void showListUnavailable();

        void insertNoteToList(Notes note, int position);

    }

    interface Presenter {

        void start();

        void deleteNote();

        void undoDeletion();

        void moveToTempContainer(Notes note, int position);

    }

}
