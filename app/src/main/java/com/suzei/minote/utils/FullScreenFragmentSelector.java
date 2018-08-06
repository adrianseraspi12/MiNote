package com.suzei.minote.utils;

import android.support.v4.app.Fragment;

import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.fullscreendialog.FullScreenEvents;
import com.suzei.minote.fullscreendialog.FullScreenLecture;
import com.suzei.minote.fullscreendialog.FullScreenReminder;
import com.suzei.minote.fullscreendialog.FullScreenTodo;

public class FullScreenFragmentSelector {

    public static Class<?extends Fragment> getFragmentClass(int type) {
        switch (type) {

            case NoteEntry.TYPE_REMINDER:
                return FullScreenReminder.class;

            case NoteEntry.TYPE_TODO:
                return FullScreenTodo.class;

            case NoteEntry.TYPE_LECTURE:
                return FullScreenLecture.class;

            case NoteEntry.TYPE_EVENTS:
                return FullScreenEvents.class;

            default:
                throw new IllegalArgumentException("Not valid type");
        }
    }

}
