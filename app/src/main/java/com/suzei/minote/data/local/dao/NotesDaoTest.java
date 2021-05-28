package com.suzei.minote.data.dao;

import android.content.Context;

import com.suzei.minote.data.NotesDatabase;
import com.suzei.minote.data.entity.Notes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class NotesDaoTest {

    private static final Notes NOTE = new Notes(
            0, "This is a title",
            "1234", "This is a message",
            "#FFFFFF", "#002071");

    private NotesDatabase notesDatabase;

    @Before
    public void initDatabase() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        //  We use inMemoryDatabaseBuilder because we don't want to store an information when
        //  the process gets killed
        notesDatabase = Room.inMemoryDatabaseBuilder(context, NotesDatabase.class).build();
    }

    @After
    public void closeDatabase() {
        notesDatabase.close();
    }

    @Test
    public void saveNoteAndGetNoteById() {
        //  Insert the Note
        notesDatabase.notesDao().saveNote(NOTE);

        //  Get the note from the database
        Notes note = notesDatabase.notesDao().findNoteById(NOTE.getId());

        //  Check the Note details
        assertNote(note,
                NOTE.getId(),
                NOTE.getTitle(),
                NOTE.getMessage(),
                NOTE.getPassword(),
                NOTE.getColor(),
                NOTE.getTextColor());
    }

    @Test
    public void updateNoteAndGetNoteById() {
        //  Insert the Note
        notesDatabase.notesDao().saveNote(NOTE);

        //  Update the old Note
        Notes note = new Notes(
                1, "New title",
                "4321", "New message",
                "#002071", "#FFFFFF");
        notesDatabase.notesDao().saveNote(note);

        //  Load the updated note
        Notes loadedNote = notesDatabase.notesDao().findNoteById(1);

        //  Check the note
        assertNote(loadedNote,
                1, "New title",
                "New message", "4321",
                "#002071", "#FFFFFF");
    }

    @Test
    public void deleteNoteAndGetAllNotes() {
        //  Save the note
        notesDatabase.notesDao().saveNote(NOTE);

        //  Delete all notes
        notesDatabase.notesDao().deleteNote(NOTE);

        //  Get all Notes
        List<Notes> notesList = notesDatabase.notesDao().findAllNotes();

        //  Check if there are no Notes
        assertThat(notesList.size(), is(0));
    }

    private void assertNote(Notes loadedNote,
                            int id, String title,
                            String message, String password,
                            String textColor, String color) {

        assertThat(loadedNote, notNullValue());
        assertThat(loadedNote.getId(), is(id));
        assertThat(loadedNote.getTitle(), is(title));
        assertThat(loadedNote.getMessage(), is(message));
        assertThat(loadedNote.getPassword(), is(password));
        assertThat(loadedNote.getColor(), is(color));
        assertThat(loadedNote.getTextColor(), is(textColor));

    }
}
