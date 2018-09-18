package com.suzei.minote;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;

import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.db.NotesLoaderManager;
import com.suzei.minote.utils.AndroidUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditorActivity extends AppCompatActivity implements NotesLoaderManager.NoteCallbacks {

    public static final int EXISTING_NOTE_LOADER = 0;
    public static final String EXTRA_NOTE_URI = "note_uri";
    public static final String EXTRA_NOTE_COLOR = "note_color";
    public static final String EXTRA_TEXT_COLOR = "text_color";
    public static final String EXTRA_NOTE_TYPE = "note_type";

    private Uri currentNoteUri;
    private String noteColor;
    private String textColor;
    private int mNoteType;

    @BindView(R.id.editor_root) ConstraintLayout rootView;
    @BindView(R.id.editor_back_arrow) ImageButton backView;
    @BindView(R.id.editor_save) ImageButton saveView;
    @BindView(R.id.editor_text_layout) LinearLayout textLayout;
    @BindView(R.id.editor_text) EditText textView;

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
        mNoteType = getIntent().getIntExtra(EXTRA_NOTE_TYPE, -1);
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
            saveView.setColorFilter(Color.parseColor(textColor));
            textView.setTextColor(Color.parseColor(textColor));
        }
    }

    @Override
    public void finishLoad(String date, String time, String message, String location, String color) {
        rootView.setBackgroundColor(Color.parseColor(color));
        textView.setText(message);
    }

    @Override
    public void resetLoad() {
        textView.setText("");
        rootView.setBackgroundColor(Color.WHITE);
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

        AndroidUtils.hideKeyboardFrom(EditorActivity.this, rootView);
    }

    private ContentValues getContentValues() {
        int type = mNoteType;

        ContentValues values = new ContentValues();
        values.put(NoteEntry.TYPE, type);
//        values.put(NoteEntry.MESSAGE, message);
        values.put(NoteEntry.COLOR, noteColor);
        values.put("text_color", textColor);

        return values;
    }
}
