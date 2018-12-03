package com.suzei.minote.utils.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.suzei.minote.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private ClickListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void setClickListener(ClickListener listener) {
        this.listener = listener;
    }

    public void destroy() {
        listener = null;
    }

    @OnClick(R.id.bsd_edit_password)
    public void onEditPasswordClick() {
        if (listener != null) {
            listener.onEditPasswordClick();
        }
    }

    @OnClick(R.id.bsd_change_note_color)
    public void onChangeNoteColorClick() {
        if (listener != null) {
            listener.onChangeNoteColorClick();
        }
    }

    @OnClick(R.id.bsd_change_text_color)
    public void onChangeTextColor() {
        if (listener != null) {
            listener.onChangeTextColorClick();
        }
    }

    public interface ClickListener {

        void onEditPasswordClick();

        void onChangeNoteColorClick();

        void onChangeTextColorClick();

    }

}