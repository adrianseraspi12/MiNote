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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.suzei.minote.R;
import com.suzei.minote.adapter.NotesAdapter;
import com.suzei.minote.adapter.NotesCursorAdapter;
import com.suzei.minote.db.NoteContract.NoteEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NOTE_LOADER = 0;

    private NotesCursorAdapter mAdapter;
    private Unbinder unbinder;

    private View mView;
    private LoaderManager loaderManager;

    @BindView(R.id.all_notes) RecyclerView noteList;
    @BindView(R.id.empty_list) AppCompatTextView emptyView;

    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_notes_list, container, false);
        initObjects();
        setUpRecyclerView();
        setUpAdapters();
        loaderStarts();
        return mView;
    }

    private void initObjects() {
        getActivity().setTitle("Events");
        unbinder = ButterKnife.bind(this, mView);
        loaderManager = getLoaderManager();
    }

    private void setUpRecyclerView() {
        noteList.setLayoutManager(new LinearLayoutManager(getContext()));
        noteList.setHasFixedSize(true);
    }

    private void setUpAdapters() {
        mAdapter = new NotesCursorAdapter(getContext(), null);
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
                NoteEntry.TIME, NoteEntry.MESSAGE, NoteEntry.LOCATION, NoteEntry.COLOR};
        String selection = NoteEntry.TYPE + "=" + NoteEntry.TYPE_EVENTS;

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

    @Override
    public void onStart() {
        super.onStart();
        unbinder.unbind();
    }

}
