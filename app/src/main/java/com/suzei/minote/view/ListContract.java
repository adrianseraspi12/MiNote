package com.suzei.minote.view;


import com.suzei.minote.data.Notes;

import java.util.List;

public interface ListContract {

    interface View {

        void setPresenter(Presenter presenter);

        void showListOfNotes(List<Notes> listOfNotes);

    }

    interface Presenter {

        void start();

    }

}
