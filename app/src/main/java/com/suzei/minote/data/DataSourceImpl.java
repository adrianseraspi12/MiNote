package com.suzei.minote.data;

import android.content.Context;
import android.os.AsyncTask;

import com.suzei.minote.utils.executors.AppExecutor;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DataSourceImpl implements DataSource {

    private NotesDao notesDao;

    private AppExecutor appExecutor;

    public DataSourceImpl(Context context) {
        notesDao = NotesDatabase.getDatabase(context).notesDao();
        appExecutor = AppExecutor.getInstance();
    }

    @Override
    public void deleteNote(Notes note) {
        Runnable runnable = () -> notesDao.deleteNote(note);
        appExecutor.getDiskIO().execute(runnable);
    }

    @Override
    public void getListOfNotes(Listener listener) {
        Runnable runnable = () -> {

            List<Notes> listOfNotes = notesDao.findAllNotes();

            appExecutor.getMainThread().execute(() -> {

                if (listOfNotes.isEmpty()) {
                    listener.onDataUnavailable();
                } else {
                    listener.onDataAvailable(listOfNotes);
                }

            });

        };

        appExecutor.getDiskIO().execute(runnable);
    }



}
