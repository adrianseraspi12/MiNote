package com.suzei.minote.ui.editor.note

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import com.suzei.minote.BaseUnitTest
import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Notes
import com.suzei.minote.data.repository.DataSource
import com.suzei.minote.data.repository.NoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.threeten.bp.OffsetDateTime
import java.lang.Exception

@ExperimentalCoroutinesApi
class EditorNotePresenterTest : BaseUnitTest() {
    private lateinit var editorPresenterWithId: EditorNotePresenter
    private lateinit var editorPresenterNewNote: EditorNotePresenter
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var mViewWithId: MockEditorNoteView
    private lateinit var mViewNoId: MockEditorNoteView
    private lateinit var repository: DataSource<Notes>

    private val note: Notes = Notes(
        "1", "Title 1",
        "Password 1", "Message 1",
        "#FFFFFF", "#0d46a0", OffsetDateTime.now()
    )

    @Before
    fun setUpEditorPresenter() = runBlockingTest {
        Dispatchers.setMain(TestCoroutineDispatcher())
        val handler = mock(Handler::class.java)
        repository = mock(NoteDataSource::class.java)
        mViewWithId = MockEditorNoteView()
        mViewNoId = MockEditorNoteView()
        sharedPrefs = mock(SharedPreferences::class.java)

        editorPresenterWithId =
            EditorNotePresenter(note.id!!, sharedPrefs, repository, mViewWithId, handler)
        editorPresenterNewNote = EditorNotePresenter(repository, sharedPrefs, mViewNoId, handler)
    }

    @Test
    fun createEditorPresenter_withNoteId() {
        editorPresenterNewNote.setup()
        Assert.assertNotNull(mViewNoId.resultPresenter)
    }

    @Test
    fun createEditorPresenter_newNote() {
        editorPresenterNewNote.setup()
        Assert.assertNotNull(mViewWithId.resultPresenter)
    }

    @Test
    fun showNoteDetailsWithIdSuccess() = runBlockingTest {
        `when`(repository.getData(note.id!!)).thenReturn(Result.Success(note))
        editorPresenterWithId.setup()
        Assert.assertNotNull(mViewWithId.resultShowDetails)
    }

    @Test
    fun showNoteDetailsWithIdError() = runBlockingTest {
        `when`(repository.getData(note.id!!)).thenReturn(Result.Error(Exception()))
        editorPresenterWithId.setup()
        Assert.assertNull(mViewWithId.resultShowDetails)
        Assert.assertEquals(mViewWithId.resultMessage, "Unable to find the note. Please try again.")
    }

    @Test
    fun showNoteDetailsWithoutId() = runBlockingTest {
        editorPresenterNewNote.setup()
        Assert.assertEquals(mViewNoId.resultSetNoteColor, Color.parseColor("#FF6464"))
        Assert.assertEquals(mViewNoId.resultSetTextColor, Color.parseColor("#000000"))
    }

    @Test
    fun saveNoteDetailsWithIdSuccess() = runBlockingTest {
        //  Setup
        `when`(repository.getData(note.id!!)).thenReturn(Result.Success(note))
        editorPresenterWithId.setup()


        `when`(repository.update(note)).thenReturn(Result.Success())
        editorPresenterWithId.saveNote(
            note.title!!,
            note.message!!,
            note.color!!,
            note.textColor!!,
            note.password
        )
        Assert.assertEquals(mViewWithId.resultMessage, "Note saved")
    }
}