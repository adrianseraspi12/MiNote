package com.suzei.minote.utils.dialogs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.suzei.minote.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordDialog extends DialogFragment {

    private static final int PASSWORD_LENGTH = 4;

    private PasswordDialogListener listener;

    private String password = "";

    @BindView(R.id.password_dots) EditText passwordView;

    public static PasswordDialog getInstance() {
        return new PasswordDialog();
    }

    public PasswordDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.PasswordDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fullscreen_dialog_password, container, false);
        ButterKnife.bind(this, view);
        return view;
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