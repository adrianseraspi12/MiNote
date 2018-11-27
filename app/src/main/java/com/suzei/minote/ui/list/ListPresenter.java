package com.suzei.minote.ui.list;

import com.suzei.minote.data.DataSource;
import com.suzei.minote.data.DataSourceImpl;
import com.suzei.minote.data.Notes;

import java.util.List;

public class ListPresenter implements ListContract.Presenter {

    private ListContract.View mView;

    private DataSourceImpl dataSourceImpl;

    private Notes tempNote;

    private int tempPosition;

    ListPresenter(DataSourceImpl dataSourceImpl, ListContract.View mView) {
        this.dataSourceImpl = dataSourceImpl;
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        showListOfNotes();
    }

    @Override
    public void deleteNote() {
        if (tempNote != null) {
            dataSourceImpl.deleteNote(tempNote);

            tempNote = null;
            tempPosition = -1;
        }

    }

    @Override
    public void undoDeletion() {
        mView.insertNoteToList(tempNote, tempPosition);

        tempPosition = -1;
        tempNote = null;
    }

    @Override
    public void moveToTempContainer(Notes note, int position) {
        this.tempNote = note;
        this.tempPosition = position;
    }

    @Override
    public void showNoteEditor(int itemId) {
        mView.redirectToEditorActivity(itemId);
    }

    private void showListOfNotes() {
        dataSourceImpl.getListOfNotes(new DataSource.Listener<List<Notes>>() {

            @Override
            public void onDataAvailable(List<Notes> result) {
                mView.showListOfNotes(result);
            }

            @Override
            public void onDataUnavailable() {

            }

        });
    }

}
