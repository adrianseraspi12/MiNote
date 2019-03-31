package com.suzei.minote.ui.list;

import com.suzei.minote.data.DataSource;
import com.suzei.minote.data.DataSourceImpl;
import com.suzei.minote.data.entity.Notes;
import com.suzei.minote.ui.list.notes.ListNoteContract;
import com.suzei.minote.ui.list.notes.ListNotePresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class ListPresenterTest {

    private static final Notes note = new Notes(
            1, "Note title",
            "1234",
            "Note message",
            "#FFFFFF",
            "#0d46a0");

    private static List<Notes> sListOfNotes;

    private ListNotePresenter listPresenter;

    @Mock
    private DataSourceImpl dataSourceImpl;

    @Mock
    private ListNoteContract.View mView;

    @Captor
    private ArgumentCaptor<DataSource.ListNoteListener> listNoteCaptor;

    @Before
    public void setUpListPresenter() {
        MockitoAnnotations.initMocks(this);
        listPresenter = new ListNotePresenter(dataSourceImpl, mView);
        sListOfNotes = new ArrayList<>();

        sListOfNotes.add(new Notes(
                1, "First Note",
                null, "Message 1",
                "#FFFFFF", "#0d46a0"));

        sListOfNotes.add(new Notes(
                2, "Second Note",
                null, "Message 2",
                "#FFFFFF", "#0d46a0"));

        sListOfNotes.add(new Notes(
                3, "Third Note",
                null, "Message 3",
                "#FFFFFF", "#0d46a0"));
    }

    @Test
    public void createListPresenter() {
        verify(mView).setPresenter(listPresenter);
    }

    @Test
    public void showListOfNotes() {
        //  Listeners are fired
        listPresenter.start();
        verify(dataSourceImpl).getListOfNotes(listNoteCaptor.capture());

        //  Notes shown in UI
        listNoteCaptor.getValue().onDataAvailable(sListOfNotes);
        ArgumentCaptor<List> showNotesArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mView).showListOfNotes(showNotesArgumentCaptor.capture());
        assertEquals(3, showNotesArgumentCaptor.getValue().size());
    }

    @Test
    public void deleteNote() {
        listPresenter.deleteNote(note);
        verify(dataSourceImpl).deleteNote(note);
    }

    @Test
    public void showNoteEditor() {
        listPresenter.showNoteEditor(note.getId());
        verify(mView).redirectToEditorActivity(note.getId());
    }
}