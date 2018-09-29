package com.suzei.minote.logic;

import android.database.Cursor;

public interface NoteListener {

    void finished(Cursor cursor);

    void reset();

}
