package com.suzei.minote.ui.list

import com.suzei.minote.data.entity.Notes
import com.suzei.minote.data.repository.NotesRepository
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.BaseUnitTest
import com.suzei.minote.ui.list.notes.ListNoteFragment
import com.suzei.minote.ui.list.notes.ListNotePresenter
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.threeten.bp.OffsetDateTime

class ListNotePresenterTest : BaseUnitTest() {
    private lateinit var notesRepository: Repository<Notes>
    private lateinit var mView: ListContract.View<Notes>
    private lateinit var listNoteCaptor: ArgumentCaptor<Repository.ListListener<Notes>>

    private lateinit var listPresenter: ListNotePresenter
    private lateinit var listOfNotes: MutableList<Notes>

    private val note: Notes = Notes(
            "1", "Note title",
            "1234",
            "Note message",
            "#FFFFFF",
            "#0d46a0",
            OffsetDateTime.now())

    @Before
    fun setUpListPresenter() {
        notesRepository = mock(NotesRepository::class.java)
        mView = mock(ListNoteFragment::class.java)
        listPresenter = ListNotePresenter(notesRepository, mView)
        listNoteCaptor = argumentCaptor()
        listOfNotes = mutableListOf(
                Notes("1",
                        "First Note",
                        null,
                        "Message 1",
                        "#FFFFFF",
                        "#0d46a0",
                        OffsetDateTime.now()
                ),
                Notes("1",
                        "Second Note",
                        null,
                        "Message 2",
                        "#FFFFFF",
                        "#0d46a0",
                        OffsetDateTime.now()),
                Notes("3",
                        "Third Note",
                        null,
                        "Message 3",
                        "#FFFFFF",
                        "#0d46a0",
                        OffsetDateTime.now())
        )
    }

    @Test
    fun createListPresenter() {
        verify(mView).setPresenter(listPresenter)
    }

    @Test
    fun showListOfNotes() {
        //  Listeners are fired
        listPresenter.setup()
        verify(notesRepository).getListOfData(capture(listNoteCaptor))

        //  Notes shown in UI
        listNoteCaptor.value.onDataAvailable(listOfNotes)
        val showNotesArgumentCaptor = argumentCaptor<MutableList<Notes>>()
        verify(mView)?.showListOfNotes(capture(showNotesArgumentCaptor))
        Assert.assertEquals(3, showNotesArgumentCaptor.value.size.toLong())
    }

    @Test
    fun deleteNote() {
        listPresenter.delete(note)
        verify(notesRepository).delete(note)
    }
}