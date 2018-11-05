package com.suzei.minote.view;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.suzei.minote.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordDialog extends Dialog {

    private static final int PASSWORD_LENGTH = 4;

    private PasswordDialogListener listener;

    private String password = "";

    @BindView(R.id.password_dots) EditText passwordView;

    public PasswordDialog(@NonNull Context context) {
        super(context, R.style.Theme_AppCompat_Light_DialogWhenLarge);
        setContentView(R.layout.fullscreen_dialog_password);
        ButterKnife.bind(this, this);
    }

    @OnClick({R.id.buttonOne, R.id.buttonTwo, R.id.buttonThree, R.id.buttonFour, R.id.buttonFive,
            R.id.buttonSix, R.id.buttonSeven, R.id.buttonEight, R.id.buttonNine, R.id.buttonZero,
            R.id.buttonClear})
    public void onNumbersClick(Button button) {
        String textNumber = button.getText().toString();

        if (textNumber.equals("CLEAR") && !TextUtils.isEmpty(password)) {
            password = password.substring(0, password.length() -1);
            passwordView.setText(password);
            return;
        }

        passwordView.append(textNumber);
        password += textNumber;

        if (password.length() == PASSWORD_LENGTH) {
            listener.onClose(password);
            dismiss();
        }

    }

    public void setOnClosePasswordDialog(PasswordDialogListener listener) {
        this.listener = listener;
    }

    public interface PasswordDialogListener {

        void onClose(String password);

    }
}
