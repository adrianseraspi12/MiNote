package com.suzei.minote.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class NoteContract {

    public static final String CONTENT_AUTHORITY = "com.suzei.minote";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
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
        public static final String TITLE = "title";
        public static final String PASSWORD = "password";
        public static final String MESSAGE = "message";
        public static final String TEXT_COLOR = "text_color";
        public static final String COLOR ="color";

        //  remove from the database
        public static final String TYPE = "type";
    }
}
