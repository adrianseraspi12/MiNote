package com.suzei.minote.data;

import android.content.Context;

import com.suzei.minote.data.dao.NotesDao;
import com.suzei.minote.data.entity.Notes;
import com.suzei.minote.utils.executors.AppExecutor;

import java.util.List;

public class DataSourceImpl implements DataSource {

    private NotesDao notesDao;

    private AppExecutor appExecutor;

    public DataSourceImpl(Context context) {
        notesDao = NotesDatabase.getDatabase(context).notesDao();
        appExecutor = AppExecutor.getInstance();
    }

    @Override
    public void saveNote(Notes note) {
        Runnable runnable = () -> notesDao.saveNote(note);
        appExecutor.getDiskIO().execute(runnable);
    }

    @Override
    public void getNote(int itemId, Listener<Notes> listener) {
        Runnable runnable = () -> {
            Notes note = notesDao.findNoteById(itemId);

            appExecutor.getMainThread().execute(() -> {
                if (note == null) {
                    listener.onDataUnavailable();
                }
                else {
                    listener.onDataAvailable(note);
                }
            });
        };

        appExecutor.getDiskIO().execute(runnable);
    }

    @Override
    public void deleteNote(Notes note) {
        Runnable runnable = () -> notesDao.deleteNote(note);
        appExecutor.getDiskIO().execute(runnable);
    }

    @Override
    public void getListOfNotes(Listener<List<Notes>> listener) {
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
