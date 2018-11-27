package com.suzei.minote.ui.editor;


import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.suzei.minote.R;
import com.suzei.minote.data.Notes;
import com.suzei.minote.utils.KeyboardUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditorFragment extends Fragment implements EditorContract.View {

    private EditorContract.Presenter presenter;

    @BindView(R.id.editor_root) ConstraintLayout rootView;
    @BindView(R.id.editor_title) EditText titleView;
    @BindView(R.id.editor_back_arrow) ImageButton backView;
    @BindView(R.id.editor_save) ImageButton saveView;
    @BindView(R.id.editor_text_layout) LinearLayout textLayout;
    @BindView(R.id.editor_text) EditText textView;
    @BindView(R.id.editor_password) ImageButton passwordView;

    static EditorFragment newInstance() {
        return new EditorFragment();
    }

    public EditorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @OnClick(R.id.editor_back_arrow)
    public void onBackClick() {
        getActivity().finish();
    }

    @OnClick(R.id.editor_text_layout)
    public void onTextFieldClick() {
        textView.requestFocus();
        KeyboardUtils.showKeyboard(getContext(), textView);
    }

    @OnClick(R.id.editor_save)
    public void onSaveClick() {

    }

    @Override
    public void setPresenter(EditorContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showNoteDetails(Notes note) {
        titleView.setText(note.getTitle());
        textView.setText(note.getMessage());
        rootView.setBackgroundColor(Color.parseColor(note.getColor()));
        titleView.setTextColor(Color.parseColor(note.getTextColor()));
        textView.setTextColor(Color.parseColor(note.getTextColor()));
    }

}
