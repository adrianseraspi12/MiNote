package com.suzei.minote.ui.editor;

import android.content.SharedPreferences;
import android.graphics.Color;

import com.suzei.minote.data.entity.Notes;
import com.suzei.minote.ui.editor.note.EditorNoteContract;
import com.suzei.minote.ui.editor.note.EditorNotePresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditorPresenterTest {

//    private static final Notes note = new Notes(
//            1, "Title 1",
//            "Password 1", "Message 1",
//            "#FFFFFF", "#0d46a0");
//
//    private EditorNotePresenter editorPresenterWithId;
//    private EditorNotePresenter editorPresenterNewNote;
//
//    @Mock
//    private EditorNoteContract.View mView;
//
//    @Mock
//    private DataSourceImpl mDataSourceImpl;
//
//    @Mock
//    private SharedPreferences sharedPrefs;
//
//    @Captor
//    private ArgumentCaptor<DataSource.NoteListener> notesListenerArgumentCaptor;
//
//    @Captor
//    private ArgumentCaptor<ColorWheel> colorWheelArgumentCaptor;
//
//    @Before
//    public void setUpEditorPresenter() {
//        MockitoAnnotations.initMocks(this);
//        editorPresenterWithId = new EditorNotePresenter(note.getId(), mDataSourceImpl, mView);
//        editorPresenterNewNote = new EditorNotePresenter(sharedPrefs, mDataSourceImpl, mView);
//    }
//
//    @Test
//    public void createEditorPresenter_withNoteId() {
//        verify(mView).setPresenter(editorPresenterWithId);
//    }
//
//    @Test
//    public void createEditorPresenter_newNote() {
//        verify(mView).setPresenter(editorPresenterNewNote);
//    }
//
//    @Test
//    public void showNoteDetailsWithId() {
//        editorPresenterWithId.start();
//        verify(mDataSourceImpl).getNote(eq(note.getId()), notesListenerArgumentCaptor.capture());
//
//        //  Note details shown in UI
//        notesListenerArgumentCaptor.getValue().onDataAvailable(note);
//        ArgumentCaptor<Notes> notesArgumentCaptor = ArgumentCaptor.forClass(Notes.class);
//        verify(mView).showNoteDetails(notesArgumentCaptor.capture());
//    }
//
//    @Test
//    public void showNewNote() {
//        editorPresenterNewNote.start();
//
//        //  Create constant for testing
//        String noteColor = "#ef5350";
//        String textColor = "#000000";
//
//        when(sharedPrefs.getString(eq("default_note_color"), eq("#ef5350")))
//                .thenReturn(noteColor);
//        when(sharedPrefs.getString(eq("default_text_color"), eq("#000000")))
//                .thenReturn(textColor);
//
//        verify(mView).textColor(Color.parseColor(noteColor));
//        verify(mView).noteColor(Color.parseColor(textColor));
//    }
//
//    @Test
//    public void saveNoteWithId() {
//        editorPresenterWithId.saveNote(
//                note.getTitle(),
//                note.getMessage(),
//                note.getColor(),
//                note.getTextColor(),
//                note.getPassword());
//
//        verify(mDataSourceImpl).saveNote(any(Notes.class));
//        verify(mView).showToastMessage("Note updated");
//    }
//
//    @Test
//    public void saveNewNote() {
//        editorPresenterNewNote.saveNote(
//                note.getTitle(),
//                note.getMessage(),
//                note.getColor(),
//                note.getTextColor(),
//                note.getPassword());
//
//        verify(mDataSourceImpl).saveNote(any(Notes.class));
//        verify(mView).showToastMessage("Note created");
//    }
//
//    @Test
//    public void showPasswordDialog_editorPresenterWithId() {
//        editorPresenterWithId.passwordDialog();
//        verify(mView).showPasswordDialog();
//    }
//
//    @Test
//    public void showPasswordDialog_editorPresenterNewNote() {
//        editorPresenterNewNote.passwordDialog();
//        verify(mView).showPasswordDialog();
//    }
//
//    @Test
//    public void noteColorWheel_editorPresenterWithId() {
//        editorPresenterWithId.noteColorWheel(123);
//        verify(mView).showColorWheel(
//                eq("Choose note color"),
//                eq(123),
//                colorWheelArgumentCaptor.capture());
//    }
//
//    @Test
//    public void noteColorWheel_editorPresenterNewNote() {
//        editorPresenterNewNote.noteColorWheel(123);
//        verify(mView).showColorWheel(
//                eq("Choose note color"),
//                eq(123),
//                colorWheelArgumentCaptor.capture());
//    }
//
//    @Test
//    public void textColorWheel_editorPresenterWithId() {
//        editorPresenterWithId.textColorWheel(123);
//        verify(mView).showColorWheel(
//                eq("Choose text color"),
//                eq(123),
//                colorWheelArgumentCaptor.capture());
//    }
//
//    @Test
//    public void textColorWheel_editorPresenterNewNote() {
//        editorPresenterNewNote.textColorWheel(123);
//        verify(mView).showColorWheel(
//                eq("Choose text color"),
//                eq(123),
//                colorWheelArgumentCaptor.capture());
//    }

}