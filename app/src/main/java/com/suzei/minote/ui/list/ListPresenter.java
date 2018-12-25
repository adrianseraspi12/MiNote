package com.suzei.minote.ui.list;

import com.suzei.minote.data.DataSource;
import com.suzei.minote.data.entity.Notes;

import java.util.List;

public class ListPresenter implements ListContract.Presenter {

    private ListContract.View mView;

    private DataSource dataSourceImpl;

    ListPresenter(DataSource dataSourceImpl, ListContract.View mView) {
        this.dataSourceImpl = dataSourceImpl;
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        showListOfNotes();
    }

    @Override
    public void deleteNote(Notes note) {
        dataSourceImpl.deleteNote(note);
    }

    @Override
    public void showNoteEditor(int itemId) {
        mView.redirectToEditorActivity(itemId);
    }

    private void showListOfNotes() {
        dataSourceImpl.getListOfNotes(new DataSource.ListNoteListener() {
            @Override
            public void onDataAvailable(List<Notes> listOfNote) {
                mView.showListOfNotes(listOfNote);
            }

            @Override
            public void onDataUnavailable() {

            }
        });
    }

}
