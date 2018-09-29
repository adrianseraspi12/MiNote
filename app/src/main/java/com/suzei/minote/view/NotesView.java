package com.suzei.minote.view;

import android.database.Cursor;

public interface NotesView {

    void showDataToUi(Cursor cursor);

    void resetLoader();

}
