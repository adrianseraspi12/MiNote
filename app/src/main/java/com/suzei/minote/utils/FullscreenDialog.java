package com.suzei.minote.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.suzei.minote.EditorActivity;
import com.suzei.minote.R;
import com.suzei.minote.db.NoteContract.NoteEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FullscreenDialog extends Dialog {

    private String colorText = "#000000";
    private String colorNote = "#ef5350";
    private String note;

    @BindView(R.id.fullscreen_dialog_note_color_layout)
    LinearLayout noteColorLayout;
    @BindView(R.id.fullscreen_dialog_text_color_layout)
    LinearLayout textColorLayout;
    @BindView(R.id.fullscreen_dialog_note_color)
    View noteColor;
    @BindView(R.id.fullscreen_dialog_text_color)
    View textColor;
    @BindView(R.id.fullscreen_dialog_note_spinner)
    AppCompatSpinner noteType;
    @BindView(R.id.fullscreen_dialog_close)
    ImageButton closeView;
    @BindView(R.id.fullscreen_dialog_note_create)
    TextView createView;

    public FullscreenDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.fullscreen_dialog);
        ButterKnife.bind(this, this);
        setUpSpinner();
    }

    private void setUpSpinner() {
        final String[] notes = getContext().getResources().getStringArray(R.array.notes);
        ArrayAdapter<String> notesAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, notes);
        noteType.setAdapter(notesAdapter);
        noteType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                note = notes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    @OnClick(R.id.fullscreen_dialog_close)
    public void onCloseClick() {
        Toast.makeText(getContext(), "Close", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @OnClick(R.id.fullscreen_dialog_note_create)
    public void onCreateClick() {
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_NOTE_TYPE, getNoteType());
        intent.putExtra(EditorActivity.EXTRA_TEXT_COLOR, colorText);
        intent.putExtra(EditorActivity.EXTRA_NOTE_COLOR, colorNote);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.fullscreen_dialog_note_color_layout)
    public void onNoteColorPick() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose note color")
                .initialColor(R.color.colorRed)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setPositiveButton("Choose", new ColorPickerClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int _color, Integer[] integers) {
                        colorNote = String.format("#%06X", (0xFFFFFF & _color));
                        noteColor.setBackgroundColor(_color);
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                })
                .build()
                .show();
    }

    @OnClick(R.id.fullscreen_dialog_text_color_layout)
    public void onTextColorPick() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose text color")
                .initialColor(R.color.black)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setPositiveButton("Choose", new ColorPickerClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int _color, Integer[] integers) {
                        colorText = String.format("#%06X", (0xFFFFFF & _color));

                        textColor.setBackgroundColor(_color);
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                })
                .build()
                .show();
    }

    private int getNoteType() {
        switch (note) {

            case "Reminder":
                return NoteEntry.TYPE_REMINDER;
            case "To-do list":
                return NoteEntry.TYPE_TODO;
            case "Lecture":
                return NoteEntry.TYPE_LECTURE;
            case "Events":
                return NoteEntry.TYPE_EVENTS;
            default:
                throw new IllegalArgumentException("Invalid note=" + note);
        }
    }

}
