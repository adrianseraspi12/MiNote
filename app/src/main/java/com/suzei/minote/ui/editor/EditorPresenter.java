package com.suzei.minote.ui.editor;

import com.suzei.minote.data.DataSource;
import com.suzei.minote.data.DataSourceImpl;
import com.suzei.minote.data.Notes;

public class EditorPresenter implements EditorContract.Presenter {

    private DataSourceImpl dataSourceImpl;

    private EditorContract.View mView;

    private int itemId;

    EditorPresenter(int itemId, DataSourceImpl dataSourceImpl, EditorContract.View mView) {
        this.dataSourceImpl = dataSourceImpl;
        this.mView = mView;
        this.itemId = itemId;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (itemId != -1) {
            showNote();
        }
    }

    @Override
    public void saveNote(Notes note) {

    }

    @Override
    public void addPasswordToNote(Notes note) {

    }

    private void showNote() {
        dataSourceImpl.getNote(itemId, new DataSource.Listener<Notes>() {

            @Override
            public void onDataAvailable(Notes result) {
                mView.showNoteDetails(result);
            }

            @Override
            public void onDataUnavailable() {

            }

        });
    }

}
