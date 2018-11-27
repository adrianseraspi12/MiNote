package com.suzei.minote.ui.editor;

import com.suzei.minote.data.Notes;

public interface EditorContract {

    interface View {

        void setPresenter(Presenter presenter);

        void showNoteDetails(Notes note);

    }

    interface Presenter {

        void start();

        void saveNote(Notes note);

        void addPasswordToNote(Notes note);

    }

}
