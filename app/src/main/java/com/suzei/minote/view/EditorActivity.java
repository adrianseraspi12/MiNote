package com.suzei.minote.view;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.suzei.minote.R;
import com.suzei.minote.data.NoteContract.NoteEntry;
import com.suzei.minote.logic.Controller;
import com.suzei.minote.utils.JsonConvert;
import com.suzei.minote.utils.KeyboardUtils;
import com.suzei.minote.utils.Turing;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditorActivity extends AppCompatActivity implements NotesView {

    public static final String EXTRA_NOTE_URI = "note_uri";
    public static final String EXTRA_NOTE_COLOR = "note_color";
    public static final String EXTRA_TEXT_COLOR = "text_color";

    private Controller controller;

    private Uri currentNoteUri;
    private String mPassword;
    private String noteColor;
    private String textColor;

    @BindView(R.id.editor_root) ConstraintLayout rootView;
    @BindView(R.id.editor_title) EditText titleView;
    @BindView(R.id.editor_back_arrow) ImageButton backView;
    @BindView(R.id.editor_save) ImageButton saveView;
    @BindView(R.id.editor_text_layout) LinearLayout textLayout;
    @BindView(R.id.editor_text) EditText textView;
    @BindView(R.id.editor_password) ImageButton passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        changeStatusBarColor();
        intBundleData();
        initObjects();
        initLoaderManager();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    private void intBundleData() {
        noteColor = getIntent().getStringExtra(EXTRA_NOTE_COLOR);
        textColor = getIntent().getStringExtra(EXTRA_TEXT_COLOR);

        String noteUri = getIntent().getStringExtra(EXTRA_NOTE_URI);
        if (noteUri !=null) {
            currentNoteUri = Uri.parse(noteUri);
        }

        controller = new Controller(EditorActivity.this, currentNoteUri, this);
    }

    private void initObjects() {
        ButterKnife.bind(this);
    }

    private void initLoaderManager() {
        if (currentNoteUri != null) {
            controller = new Controller(EditorActivity.this, currentNoteUri, this);
            controller.init();
        } else {
            rootView.setBackgroundColor(Color.parseColor(noteColor));
            backView.setColorFilter(Color.parseColor(textColor));
            passwordView.setColorFilter(Color.parseColor(textColor));
            saveView.setColorFilter(Color.parseColor(textColor));
            textView.setTextColor(Color.parseColor(textColor));
            titleView.setTextColor(Color.parseColor(textColor));
        }
    }

    @OnClick(R.id.editor_password)
    public void onPasswordClick() {
        PasswordDialog passwordDialog = new PasswordDialog(EditorActivity.this);
        passwordDialog.setOnClosePasswordDialog(password -> mPassword = password);
        passwordDialog.show();
    }

    @OnClick(R.id.editor_back_arrow)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.editor_text_layout)
    public void onTextFieldClick() {
        textView.requestFocus();
        KeyboardUtils.showKeyboard(EditorActivity.this, textView);
    }

    @OnClick(R.id.editor_save)
    public void onSaveClick() {
        ContentValues values = getContentValues();
        if (values == null) {
            return;
        }

        if (currentNoteUri != null) {
            String selection = NoteEntry._ID + "=?";
            String[] selectionArgs =
                    new String[]{String.valueOf(ContentUris.parseId(currentNoteUri))};
            int rowsUpdated = getContentResolver().update(NoteEntry.CONTENT_URI, values,
                    selection, selectionArgs);
            if (rowsUpdated != 0) {
                Toast.makeText(
                        EditorActivity.this,
                        R.string.note_updated,
                        Toast.LENGTH_SHORT).show();
            }

        }

        else {
            Uri insertUri = getContentResolver().insert(NoteEntry.CONTENT_URI, values);

            if (insertUri == null) {
                Toast.makeText(
                        EditorActivity.this,
                        R.string.note_not_saved,
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(
                        EditorActivity.this,
                        R.string.note_save,
                        Toast.LENGTH_SHORT).show();
            }
        }

        KeyboardUtils.hideKeyboard(EditorActivity.this, rootView);
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(NoteEntry.TITLE, titleView.getText().toString().trim());
        values.put(NoteEntry.MESSAGE, textView.getText().toString().trim());
        values.put(NoteEntry.COLOR, noteColor);
        values.put(NoteEntry.TEXT_COLOR, textColor);

        if (!TextUtils.isEmpty(mPassword)) {
            values.put(NoteEntry.PASSWORD, Turing.encrypt(mPassword));
        }

        return values;
    }

    @Override
    public void showDataToUi(Cursor cursor) {
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(NoteEntry.TITLE));
            String password = cursor.getString(cursor.getColumnIndex(NoteEntry.PASSWORD));
            String message = cursor.getString(cursor.getColumnIndex(NoteEntry.MESSAGE));
            noteColor = cursor.getString(cursor.getColumnIndex(NoteEntry.COLOR));
            textColor = cursor.getString(cursor.getColumnIndex(NoteEntry.TEXT_COLOR));

            mPassword = Turing.decrypt(password);

            if (JsonConvert.isValidJson(message)) {
                message = JsonConvert.toString(message);
            }

            textView.setText(message);
            titleView.setText(title);

            rootView.setBackgroundColor(Color.parseColor(noteColor));

            if (textColor != null) {
                textView.setTextColor(Color.parseColor(textColor));
                backView.setColorFilter(Color.parseColor(textColor));
                passwordView.setColorFilter(Color.parseColor(textColor));
                saveView.setColorFilter(Color.parseColor(textColor));
                titleView.setTextColor(Color.parseColor(textColor));
            } else {
                textView.setTextColor(Color.parseColor("#000000"));
                backView.setColorFilter(Color.parseColor("#000000"));
                passwordView.setColorFilter(Color.parseColor("#000000"));
                saveView.setColorFilter(Color.parseColor("#000000"));
                titleView.setTextColor(Color.parseColor("#000000"));
                this.textColor = "#000000";
            }
        }
    }

    @Override
    public void resetLoader() {
        textView.setText("");
        titleView.setText("");
        rootView.setBackgroundColor(Color.WHITE);
    }

}
