package com.suzei.minote.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.suzei.minote.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickColorDialog {

    private String colorText = "#000000";
    private String colorNote = "#ef5350";

    private final Activity activity;
    private final AlertDialog.Builder alertDialogBuilder;

    @BindView(R.id.dialog_note_color)
    View noteColor;

    @BindView(R.id.dialog_text_color)
    View textColor;

    PickColorDialog(Activity activity) {
        this.activity = activity;
        alertDialogBuilder = new AlertDialog.Builder(activity);
    }

    public void show() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.dialog_choose_color, null);

        ButterKnife.bind(this, convertView);
        alertDialogBuilder.setView(convertView);
        alertDialogBuilder.setTitle(R.string.choose_color);

        alertDialogBuilder.setPositiveButton(R.string.choose, (dialog, which) -> {

            Intent intent = new Intent(activity, EditorActivity.class);
            intent.putExtra(EditorActivity.EXTRA_TEXT_COLOR, colorText);
            intent.putExtra(EditorActivity.EXTRA_NOTE_COLOR, colorNote);
            activity.startActivity(intent);

        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        alertDialogBuilder.show();
    }

    @OnClick(R.id.dialog_note_color_layout)
    public void onNoteColorPick() {
        ColorPickerDialogBuilder
                .with(activity)
                .setTitle(R.string.choose_note_color)
                .initialColor(R.color.colorRed)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(6)
                .setPositiveButton(R.string.choose, (dialogInterface, _color, integers) -> {

                    colorNote = String.format("#%06X", (0xFFFFFF & _color));
                    noteColor.setBackgroundColor(_color);

                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .build()
                .show();
    }

    @OnClick(R.id.dialog_text_color_layout)
    public void onTextColorPick() {
        ColorPickerDialogBuilder
                .with(activity)
                .setTitle(R.string.choose_text_color)
                .initialColor(R.color.black)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(6)
                .setPositiveButton(R.string.choose, (dialogInterface, _color, integers) -> {
                    colorText = String.format("#%06X", (0xFFFFFF & _color));
                    textColor.setBackgroundColor(_color);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .build()
                .show();
    }
}
