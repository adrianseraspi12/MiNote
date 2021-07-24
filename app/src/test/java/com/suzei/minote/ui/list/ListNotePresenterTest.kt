package com.suzei.minote.ui.list

import com.suzei.minote.BaseUnitTest
import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Notes
import com.suzei.minote.data.repository.DataSource
import com.suzei.minote.data.repository.NoteDataSource
import com.suzei.minote.ui.list.notes.ListNotePresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.threeten.bp.OffsetDateTime

@ExperimentalCoroutinesApi
class ListNotePresenterTest : BaseUnitTest() {
    private lateinit var notesRepository: DataSource<Notes>
    private lateinit var mView: MockListView<Notes>

    private lateinit var listPresenter: ListNotePresenter
    private lateinit var listOfNotes: List<Notes>

    @Before
    fun setUpListPresenter() = runBlockingTest {
        notesRepository = mock(NoteDataSource::class.java)
        mView = MockListView()
        listPresenter = ListNotePresenter(notesRepository, mView)
        Dispatchers.setMain(TestCoroutineDispatcher())
        listOfNotes = listOf(
            Notes(
                "1",
                "First Note",
                null,
                "Message 1",
                "#FFFFFF",
                "#0d46a0",
                OffsetDateTime.now()
            ),
            Notes(
                "1",
                "Second Note",
                null,
                "Message 2",
                "#FFFFFF",
                "#0d46a0",
                OffsetDateTime.now()
            ),
            Notes(
                "3",
                "Third Note",
                null,
                "Message 3",
                "#FFFFFF",
                "#0d46a0",
                OffsetDateTime.now()
            )
        )
    }

    @Test
    fun createListPresenter() {
        Assert.assertNotNull(mView.resultPresenter)
    }

    @Test
    fun showListOfNotesSuccess() = runBlockingTest {
        `when`(notesRepository.getListOfData()).thenReturn(Result.Success(listOfNotes))
        listPresenter.setup()
        Assert.assertEquals(listOfNotes, mView.listOfNotes)
    }

    @Test
    fun showListOfNotesError() = runBlockingTest {
        `when`(notesRepository.getListOfData()).thenReturn(Result.Error(Exception()))
        listPresenter.setup()
        Assert.assertNull(mView.listOfNotes)
    }
}