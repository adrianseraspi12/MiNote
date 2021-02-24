package com.suzei.minote.ui.editor

import android.graphics.Color
import com.suzei.minote.BaseUnitTest
import com.suzei.minote.data.entity.Notes
import com.suzei.minote.data.repository.NotesRepository
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.ui.editor.note.EditorNoteContract
import com.suzei.minote.ui.editor.note.EditorNoteFragment
import com.suzei.minote.ui.editor.note.EditorNotePresenter
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.threeten.bp.OffsetDateTime

class EditorNotePresenterTest : BaseUnitTest() {
    private lateinit var editorPresenterWithId: EditorNotePresenter
    private lateinit var editorPresenterNewNote: EditorNotePresenter

    private lateinit var mView: EditorNoteContract.View
    private lateinit var repository: Repository<Notes>
    private lateinit var notesListenerArgumentCaptor: ArgumentCaptor<Repository.Listener<Notes>>

    private val note: Notes = Notes(
            "1", "Title 1",
            "Password 1", "Message 1",
            "#FFFFFF", "#0d46a0", OffsetDateTime.now())

    @Before
    fun setUpEditorPresenter() {
        notesListenerArgumentCaptor = argumentCaptor()
        repository = mock(NotesRepository::class.java)
        mView = mock(EditorNoteFragment::class.java)
        editorPresenterWithId = EditorNotePresenter(note.id!!, repository, mView)
        editorPresenterNewNote = EditorNotePresenter(repository, mView)
        editorPresenterWithId.setup()
        editorPresenterNewNote.setup()
    }

    @Test
    fun createEditorPresenter_withNoteId() {
        verify(mView).setPresenter(editorPresenterWithId)
    }

    @Test
    fun createEditorPresenter_newNote() {
        verify(mView).setPresenter(editorPresenterNewNote)
    }

    @Test
    fun showNoteDetailsWithId() {
        verify(repository).getData(safeEq(note.id!!), capture(notesListenerArgumentCaptor))

        //  Note details shown in UI
        notesListenerArgumentCaptor.value.onDataAvailable(note)
        val notesArgumentCaptor: ArgumentCaptor<Notes> = argumentCaptor()
        verify(mView).showNoteDetails(capture(notesArgumentCaptor))
    }

    @Test
    fun showNewNote() {
        //  Default color for new note
        val noteColor = "#FF6464"
        val textColor = "#000000"

        verify(mView).setTextColor(Color.parseColor(noteColor))
        verify(mView).setNoteColor(Color.parseColor(textColor))
    }
}