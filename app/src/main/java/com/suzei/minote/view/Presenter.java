package com.suzei.minote.view;

import com.suzei.minote.data.DataSourceImpl;

public class Presenter implements ListContract.Presenter {

    private ListContract.View mView;

    private DataSourceImpl dataSourceImpl;

    public Presenter(DataSourceImpl dataSourceImpl, ListContract.View mView) {
        this.dataSourceImpl = dataSourceImpl;
        this.mView = mView;
    }

    @Override
    public void start() {
        mView.showListOfNotes(dataSourceImpl.getListOfNotes());
    }

}
