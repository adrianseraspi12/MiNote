package com.suzei.minote.ui.list;


import com.suzei.minote.data.entity.Notes;

import java.util.List;

public interface ListContract {

    interface View {

        void setPresenter(Presenter presenter);

        void showListOfNotes(List<Notes> listOfNotes);

        void showListUnavailable();

        void insertNoteToList(Notes note, int position);

        void redirectToEditorActivity(int itemId);

    }

    interface Presenter {

        void start();

        void deleteNote(Notes note);


        void showNoteEditor(int itemId);

    }

}
