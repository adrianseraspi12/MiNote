package com.suzei.minote.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class NoteContract {

    public static final String CONTENT_AUTHORITY = "com.suzei.minote";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NOTES = "notes";

    public NoteContract() {
    }

    public static abstract class NoteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

        // Mime Type of list notes
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_NOTES;

        // Mime Type of a single note
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_NOTES;

        public static final String TABLE_NAME = "notes";
        public static final String _ID = BaseColumns._ID;
        public static final String TYPE = "type";
        public static final String TITLE = "title";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String MESSAGE = "message";
        public static final String LOCATION = "location";
        public static final String COLOR ="color";

        public static final int TYPE_REMINDER = 0;
        public static final int TYPE_TODO = 1;
        public static final int TYPE_LECTURE = 2;
        public static final int TYPE_EVENTS = 3;

        public static boolean isValidType(int type) {
            return type == TYPE_REMINDER || type == TYPE_TODO || type == TYPE_LECTURE ||
                    type == TYPE_EVENTS;
        }

        public static String getTypeString(int type) {
            switch (type) {
                case TYPE_REMINDER:
                    return "Reminder";
                case TYPE_TODO:
                    return "To-do";
                case TYPE_LECTURE:
                    return "Lecture";
                case TYPE_EVENTS:
                    return "Events";
                default:
                    throw new IllegalArgumentException("Not valid type");
            }
        }
    }
}
