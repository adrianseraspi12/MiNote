package com.suzei.minote.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.suzei.minote.R;

public class ColorPicker implements View.OnClickListener {

    private Context mContext;
    private View mView;

    private Button btnRed;
    private Button btnBlue;
    private Button btnGreen;
    private Button btnOrange;
    private Button btnGray;

    private String checkString;
    private String selectedColor;

    public ColorPicker(Context mContext, View mView) {
        this.mContext = mContext;
        this.mView = mView;
        checkString = mContext.getString(R.string.check);
        initUiViews();
        setListeners();
        setDefaultColor();
    }

    private void initUiViews() {
        btnRed = mView.findViewById(R.id.btn_pick_red);
        btnBlue = mView.findViewById(R.id.btn_pick_blue);
        btnGreen = mView.findViewById(R.id.btn_pick_green);
        btnOrange = mView.findViewById(R.id.btn_pick_orange);
        btnGray = mView.findViewById(R.id.btn_pick_gray);
    }

    private void setListeners() {
        btnBlue.setOnClickListener(this);
        btnGray.setOnClickListener(this);
        btnGreen.setOnClickListener(this);
        btnOrange.setOnClickListener(this);
        btnRed.setOnClickListener(this);
    }

    private void setDefaultColor() {
        if (selectedColor == null) {
            selectedColor = getColorHex("red");
            btnRed.setText(checkString);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_pick_red:
                selectedColor = getColorHex("red");
                btnRed.setText(checkString);

                btnOrange.setText("");
                btnGreen.setText("");
                btnGray.setText("");
                btnBlue.setText("");
                break;

            case R.id.btn_pick_blue:
                selectedColor = getColorHex("blue");
                btnBlue.setText(checkString);

                btnOrange.setText("");
                btnGreen.setText("");
                btnGray.setText("");
                btnRed.setText("");
                break;

            case R.id.btn_pick_green:
                selectedColor = getColorHex("green");
                btnGreen.setText(checkString);

                btnOrange.setText("");
                btnRed.setText("");
                btnGray.setText("");
                btnBlue.setText("");
                break;

            case R.id.btn_pick_orange:
                selectedColor = getColorHex("orange");
                btnOrange.setText(checkString);

                btnRed.setText("");
                btnGreen.setText("");
                btnGray.setText("");
                btnBlue.setText("");
                break;

            case R.id.btn_pick_gray:
                selectedColor = getColorHex("gray");
                btnGray.setText(checkString);

                btnOrange.setText("");
                btnGreen.setText("");
                btnRed.setText("");
                btnBlue.setText("");
                break;
        }
    }

    private String getColorHex(String color) {
        switch (color) {
            case "red":
                return "#ef5350";
            case "blue":
                return "#42a5f5";
            case "green":
                return "#66bb6a";
            case "orange":
                return "#ffa726";
            case "gray":
                return "#78909c";
            default:
                throw new IllegalArgumentException("Not valid color");
        }
    }

    public void setSelectedColor(String hexColor) {
        switch (hexColor) {
            case "#ef5350":
                selectedColor = hexColor;
                btnRed.setText(checkString);

                btnOrange.setText("");
                btnGreen.setText("");
                btnGray.setText("");
                btnBlue.setText("");
                break;
            case "#42a5f5":
                selectedColor = hexColor;
                btnBlue.setText(checkString);

                btnOrange.setText("");
                btnGreen.setText("");
                btnGray.setText("");
                btnRed.setText("");
                break;
            case "#66bb6a":
                selectedColor = hexColor;
                btnGreen.setText(checkString);

                btnOrange.setText("");
                btnRed.setText("");
                btnGray.setText("");
                btnBlue.setText("");
                break;
            case "#ffa726":
                selectedColor = hexColor;
                btnGreen.setText(checkString);

                btnRed.setText("");
                btnGreen.setText("");
                btnGray.setText("");
                btnBlue.setText("");
                break;
            case "#78909c":
                selectedColor = hexColor;
                btnGray.setText(checkString);

                btnOrange.setText("");
                btnGreen.setText("");
                btnRed.setText("");
                btnBlue.setText("");
                break;
        }
    }

    public String getSelectedColor() {
        return selectedColor;
    }
}
