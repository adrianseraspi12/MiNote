package com.suzei.minote.view;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;

import com.suzei.minote.R;
import com.suzei.minote.utils.Turing;
import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.db.NotesLoaderManager;
import com.suzei.minote.utils.KeyboardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditorActivity extends AppCompatActivity implements NotesLoaderManager.NoteCallbacks {

    private static final String TAG = "EditorActivity";

    public static final int EXISTING_NOTE_LOADER = 0;
    public static final String EXTRA_NOTE_URI = "note_uri";
    public static final String EXTRA_NOTE_COLOR = "note_color";
    public static final String EXTRA_TEXT_COLOR = "text_color";

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
    }

    private void initObjects() {
        ButterKnife.bind(this);
    }

    private void initLoaderManager() {
        if (currentNoteUri != null) {
            NotesLoaderManager mNoteManager = new NotesLoaderManager(
                    EditorActivity.this,
                    currentNoteUri, this);

            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(EXISTING_NOTE_LOADER, null, mNoteManager);
        } else {
            rootView.setBackgroundColor(Color.parseColor(noteColor));
            backView.setColorFilter(Color.parseColor(textColor));
            passwordView.setColorFilter(Color.parseColor(textColor));
            saveView.setColorFilter(Color.parseColor(textColor));
            textView.setTextColor(Color.parseColor(textColor));
        }
    }

    @Override
    public void finishLoad(String title, String password, String message, String color, String _textColor) {
        this.noteColor = color;

        rootView.setBackgroundColor(Color.parseColor(color));
        titleView.setText(title);
        textView.setText(message);

        if (_textColor != null) {
            this.textColor = _textColor;
            backView.setColorFilter(Color.parseColor(_textColor));
            saveView.setColorFilter(Color.parseColor(_textColor));
            passwordView.setColorFilter(Color.parseColor(_textColor));
            textView.setTextColor(Color.parseColor(_textColor));
            titleView.setTextColor(Color.parseColor(_textColor));
        } else {
            this.textColor = "#000000";
        }
    }

    @Override
    public void resetLoad() {
        textView.setText("");
        rootView.setBackgroundColor(Color.WHITE);
    }

    @OnClick(R.id.editor_password)
    public void onPasswordClick() {
        //  showInputPassword;
        PasswordDialog passwordDialog = new PasswordDialog(EditorActivity.this);
        passwordDialog.setOnClosePasswordDialog(new PasswordDialog.PasswordDialogListener() {

            @Override
            public void onClose(String password) {
                mPassword = password;
                Log.i(TAG, "onClose: " + mPassword);
            }

        });

        passwordDialog.show();
    }

    @OnClick(R.id.editor_back_arrow)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.editor_text_layout)
    public void onTextFieldClick() {
        textView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
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
                Toast.makeText(EditorActivity.this, "Note Updated", Toast.LENGTH_SHORT)
                        .show();
            }

        } else {
            Uri insertUri = getContentResolver().insert(NoteEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (insertUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(EditorActivity.this, "Note not saved",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(EditorActivity.this, "Note save", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        KeyboardUtils.hideKeyboardFrom(EditorActivity.this, rootView);
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
}