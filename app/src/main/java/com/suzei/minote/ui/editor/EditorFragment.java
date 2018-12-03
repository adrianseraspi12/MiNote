package com.suzei.minote.ui.editor;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.suzei.minote.R;
import com.suzei.minote.data.entity.Notes;
import com.suzei.minote.utils.ColorWheel;
import com.suzei.minote.utils.KeyboardUtils;
import com.suzei.minote.utils.Turing;
import com.suzei.minote.utils.dialogs.BottomSheetFragment;
import com.suzei.minote.utils.dialogs.PasswordDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditorFragment extends Fragment implements EditorContract.View {

    private EditorContract.Presenter presenter;

    private String mPassword;

    @BindView(R.id.editor_root) ConstraintLayout rootView;
    @BindView(R.id.editor_title) EditText titleView;
    @BindView(R.id.editor_back_arrow) ImageButton backView;
    @BindView(R.id.editor_save) ImageButton saveView;
    @BindView(R.id.editor_text_layout) LinearLayout textLayout;
    @BindView(R.id.editor_text) EditText textView;
    @BindView(R.id.editor_menu) ImageButton menuView;

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

    @OnClick(R.id.editor_menu)
    public void onMenuClick() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.setClickListener(new BottomSheetFragment.ClickListener() {

            @Override
            public void onEditPasswordClick() {
                presenter.passwordDialog();
            }

            @Override
            public void onChangeNoteColorClick() {
                int noteColor = ((ColorDrawable) rootView.getBackground()).getColor();
                presenter.noteColorWheel(noteColor);
            }

            @Override
            public void onChangeTextColorClick() {
                int textColor = textView.getCurrentTextColor();
                presenter.textColorWheel(textColor);
            }
        });

        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
    }

    @OnClick(R.id.editor_save)
    public void onSaveClick() {
        int noteColor = ((ColorDrawable) rootView.getBackground()).getColor();
        String hexNoteColor = String.format("#%06X", (0xFFFFFF & noteColor));

        int textColor = textView.getCurrentTextColor();
        String hexTextColor = String.format("#%06X", (0xFFFFFF & textColor));

        String title = titleView.getText().toString();
        String message = textView.getText().toString();
        String password = Turing.encrypt(mPassword);

        presenter.saveNote(title, message, hexNoteColor, hexTextColor, password);
    }

    @Override
    public void setPresenter(EditorContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showNoteDetails(Notes note) {
        titleView.setText(note.getTitle());
        textView.setText(note.getMessage());
        noteColor(Color.parseColor(note.getColor()));
        textColor(Color.parseColor(note.getTextColor()));
    }

    @Override
    public void noteColor(int noteColor) {
        rootView.setBackgroundColor(noteColor);
    }

    @Override
    public void textColor(int textColor) {
        titleView.setTextColor(textColor);
        textView.setTextColor(textColor);
        backView.setColorFilter(textColor);
        saveView.setColorFilter(textColor);
        menuView.setColorFilter(textColor);
    }

    @Override
    public void showNoteSave() {
        Toast.makeText(getContext(), "Note saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showColorWheel(String title, int initialColor, ColorWheel colorWheel) {
        ColorPickerDialogBuilder.with(getContext())
                .setTitle(title)
                .initialColor(initialColor)
                .density(6)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .setPositiveButton("Choose", (
                        dialogInterface,
                        i, integers) -> colorWheel.onPositiveClick(i))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .build()
                .show();
    }

    @Override
    public void showPasswordDialog() {
        PasswordDialog passwordDialog = PasswordDialog.getInstance();
        passwordDialog.setOnClosePasswordDialog(password -> this.mPassword = password);
        passwordDialog.show(getFragmentManager(), "Password Dialog");
    }

}