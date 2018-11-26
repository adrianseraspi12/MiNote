package com.suzei.minote.data;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DataSourceImpl implements DataSource {

    private NotesDao notesDao;

    public DataSourceImpl(Context context) {
        notesDao = NotesDatabase.getDatabase(context).notesDao();
    }

    @Override
    public List<Notes> getListOfNotes() {
        try {
            return new ListTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            throw new IllegalArgumentException("List is null");
    }

    class ListTask extends AsyncTask<Void, Void, List<Notes>> {

        @Override
        protected List<Notes> doInBackground(Void... voids) {
            return notesDao.findAllNotes();
        }

    }

}
