package com.suzei.minote;

import android.content.Context;

import com.suzei.minote.data.DataSourceImpl;
import com.suzei.minote.data.NotesDatabase;
import com.suzei.minote.utils.executors.AppExecutor;

public class Injection {

    public static DataSourceImpl provideDataSourceImpl(Context context) {
        NotesDatabase notesDatabase = NotesDatabase.getInstance(context);
        return DataSourceImpl.getInstance(
                AppExecutor.getInstance(),
                notesDatabase.notesDao());
    }

}
