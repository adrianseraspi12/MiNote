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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.suzei.minote.R;
import com.suzei.minote.data.entity.Notes;
import com.suzei.minote.ui.editor.EditorActivity;
import com.suzei.minote.utils.Turing;
import com.suzei.minote.utils.widgets.RecyclerViewEmptySupport;
import com.suzei.minote.utils.dialogs.PasswordDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements ListContract.View {

    private ListContract.Presenter presenter;

    private List<Notes> listOfNotes;

    private ListAdapter listAdapter;

    private Notes tempNote;
    private Notes consecutiveNote;
    private int tempPosition;

    @BindView(R.id.list_notes)
    RecyclerViewEmptySupport noteList;
    @BindView(R.id.list_empty_placeholder)
    LinearLayout emptyView;
    @BindView(R.id.list_root)
    View rootView;

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
        startActivity(new Intent(getContext(), EditorActivity.class));
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
        listOfNotes.add(position, note);
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
            holder.messageView.setText(note.getTitle());

            if (note.getPassword() != null) {
                holder.passwordView.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(v -> showPasswordDialog(note));
            } else {
                holder.passwordView.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(v -> presenter.showNoteEditor(note.getId()));
            }

        }

        private void showPasswordDialog(Notes note) {
            String decryptedPassword = Turing.decrypt(note.getPassword());
            PasswordDialog passwordDialog = PasswordDialog.getInstance();
            passwordDialog.setOnClosePasswordDialog(password -> {

                if (!decryptedPassword.equals(password)) {
                    Toast.makeText(getContext(),
                            "Wrong Password, Please Try again",
                            Toast.LENGTH_SHORT).show();
                } else {
                    presenter.showNoteEditor(note.getId());
                }

            });
            passwordDialog.show(getFragmentManager(), "PasswordDialog");
        }

        @Override
        public int getItemCount() {
            return listOfNotes.size();
        }

        class ListViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.item_notes_color)
            View colorNote;
            @BindView(R.id.item_notes_title)
            TextView messageView;
            @BindView(R.id.item_notes_delete)
            ImageButton deleteView;
            @BindView(R.id.item_notes_password)
            ImageView passwordView;

            ListViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }

            @OnClick(R.id.item_notes_delete)
            public void onDeleteNoteClick() {
                tempPosition = getAdapterPosition();
                tempNote = listOfNotes.get(tempPosition);

                listOfNotes.remove(tempNote);
                listAdapter.notifyItemRemoved(tempPosition);

                showSnackbar();
            }

            private void showSnackbar() {
                Snackbar.make(rootView, "Note Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> insertNoteToList(tempNote, tempPosition))
                        .addCallback(new Snackbar.Callback() {

                            @Override
                            public void onShown(Snackbar sb) {
                                super.onShown(sb);
                                consecutiveNote = tempNote;
                            }

                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                switch (event) {

                                    case BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE:
                                        presenter.deleteNote(consecutiveNote);
                                        break;
                                    case BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT:
                                        presenter.deleteNote(tempNote);
                                        consecutiveNote = null;
                                        tempNote = null;
                                        tempPosition = -1;
                                        break;
                                }

                            }

                        })
                        .show();
            }

        }

    }

}
