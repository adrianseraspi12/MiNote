package com.suzei.minote.view;

import android.database.Cursor;

public interface NotesView {

    void setDataToAdapter(Cursor cursor);

    void startActivity(String noteUri);

    void resetLoader();

    void onDeleteSuccess();

    void onDeleteFailed();

    void onPasswordisInvalid();

}
