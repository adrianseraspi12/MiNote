package com.suzei.minote.ui.editor;

import com.suzei.minote.data.entity.Notes;
import com.suzei.minote.utils.ColorWheel;

public interface EditorContract {

    interface View {

        void setPresenter(Presenter presenter);

        void showNoteDetails(Notes note);

        void noteColor(int noteColor);

        void textColor(int textColor);

        void showNoteSave();

        void showColorWheel(String title, int initialColor, ColorWheel colorWheel);

        void showPasswordDialog();
    }

    interface Presenter {

        void start();

        void saveNote(String title,
                      String message,
                      String noteColor,
                      String textColor,
                      String password);

        void passwordDialog();

        void noteColorWheel(int initialColor);

        void textColorWheel(int initialColor);

    }

}
