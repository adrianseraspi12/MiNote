package com.suzei.minote.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatTextView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.suzei.minote.R;
import com.suzei.minote.adapter.NotesAdapter;
import com.suzei.minote.db.NoteContract.NoteEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReminderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NOTE_LOADER = 0;

    private View mView;
    private GridView noteList;
    private AppCompatTextView emptyView;

    private LoaderManager loaderManager;
    private NotesAdapter mAdapter;

    public ReminderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_reminder, container, false);
        initUiViews();
        initObjects();
        setUpAdapters();
        loaderStarts();
        return mView;
    }

    private void initUiViews() {
        getActivity().setTitle("Reminders");
        emptyView = mView.findViewById(R.id.empty_list);
        noteList = mView.findViewById(R.id.all_notes);
        noteList.setEmptyView(emptyView);
    }

    private void initObjects() {
        loaderManager = getLoaderManager();
    }

    private void setUpAdapters() {
        mAdapter = new NotesAdapter(getContext(), null, new NotesAdapter.DatabaseCallbacks() {

            @Override
            public void AfterDeletion() {
                loaderManager.restartLoader(NOTE_LOADER, null, ReminderFragment.this);
            }
        });
        noteList.setAdapter(mAdapter);
    }

    private void loaderStarts() {
        loaderManager.initLoader(NOTE_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri uri = NoteEntry.CONTENT_URI;
        String[] projection = {NoteEntry._ID, NoteEntry.TYPE, NoteEntry.DATE,
                NoteEntry.TIME, NoteEntry.MESSAGE, NoteEntry.COLOR};
        String selection = NoteEntry.TYPE + "=" + NoteEntry.TYPE_REMINDER;

        return new CursorLoader(getContext(), uri, projection, selection, null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
