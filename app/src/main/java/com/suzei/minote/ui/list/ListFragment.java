package com.suzei.minote.ui.list;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.suzei.minote.R;
import com.suzei.minote.data.Notes;
import com.suzei.minote.ui.editor.EditorActivity;
import com.suzei.minote.utils.RecyclerViewEmptySupport;
import com.suzei.minote.view.PickColorDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements ListContract.View {

    private ListContract.Presenter presenter;

    private List<Notes> listOfNotes;

    private ListAdapter listAdapter;

    @BindView(R.id.list_notes) RecyclerViewEmptySupport noteList;
    @BindView(R.id.list_empty_placeholder) LinearLayout emptyView;
    @BindView(R.id.list_root) View rootView;

    static ListFragment newInstance() {
        return new ListFragment();
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listOfNotes = new ArrayList<>();
        listAdapter = new ListAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        noteList.setEmptyView(emptyView);
        noteList.setLayoutManager(new LinearLayoutManager(getContext()));
        noteList.setHasFixedSize(true);
        noteList.setAdapter(listAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @OnClick(R.id.list_add_note)
    public void onAddNoteClick() {
        PickColorDialog pickColorDialog = new PickColorDialog(getActivity());
        pickColorDialog.show();
    }

    @Override
    public void setPresenter(ListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showListOfNotes(List<Notes> listOfNotes) {
        this.listOfNotes = listOfNotes;
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void showListUnavailable() {

    }

    @Override
    public void insertNoteToList(Notes note, int position) {
        listOfNotes.add(note);
        listAdapter.notifyItemInserted(position);
    }

    @Override
    public void redirectToEditorActivity(int itemId) {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_NOTE_ID, itemId);
        startActivity(intent);
    }

    class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(
                    R.layout.item_row_notes_default,
                    parent,
                    false);

            return new ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
            Notes note = listOfNotes.get(position);
            holder.colorNote.setBackgroundColor(Color.parseColor(note.getColor()));
            holder.messageView.setText(note.getMessage());
        }

        @Override
        public int getItemCount() {
            return listOfNotes.size();
        }

        class ListViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.item_notes_color) View colorNote;
            @BindView(R.id.item_notes_title) TextView messageView;
            @BindView(R.id.item_notes_delete) ImageButton deleteView;
            @BindView(R.id.item_notes_password) ImageView passwordView;

            ListViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.item_notes_delete)
            public void onDeleteNoteClick() {
                int position = getAdapterPosition();
                Notes note = listOfNotes.get(position);

                listOfNotes.remove(note);
                listAdapter.notifyItemRemoved(position);

                presenter.moveToTempContainer(note, position);
                showSnackbar();
            }

            @OnClick(R.id.item_rootview)
            public void onItemNoteClick() {
                Notes note = listOfNotes.get(getAdapterPosition());
                presenter.showNoteEditor(note.getId());
            }

            private void showSnackbar() {
                Snackbar.make(
                        rootView,
                        "Note Deleted",
                        Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> presenter.undoDeletion())
                        .addCallback(new Snackbar.Callback() {

                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                presenter.deleteNote();
                            }

                        })
                        .show();
            }

        }

    }

}
