package com.suzei.minote.ui.editor.todo

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import com.suzei.minote.BaseUnitTest
import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.repository.DataSource
import com.suzei.minote.data.repository.TodoDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.threeten.bp.OffsetDateTime
import java.lang.Exception

@ExperimentalCoroutinesApi
class EditorTodoPresenterTest : BaseUnitTest() {

    private lateinit var editorPresenterWithId: EditorTodoPresenter
    private lateinit var editorPresenterNewTodo: EditorTodoPresenter

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var mViewWithId: MockEditorTodoView
    private lateinit var mViewWithoutId: MockEditorTodoView
    private lateinit var repository: DataSource<Todo>

    private val todo: Todo = Todo(
            "1",
            "Title 1",
            listOf(),
            "#FFFFFF",
            "#0d46a0",
            OffsetDateTime.now()
    )

    @Before
    fun setUpEditorPresenter() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        repository = Mockito.mock(TodoDataSource::class.java)
        mViewWithId = MockEditorTodoView()
        mViewWithoutId = MockEditorTodoView()
        val handler = Mockito.mock(Handler::class.java)
        sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        editorPresenterWithId = EditorTodoPresenter(todo.id!!, sharedPrefs, repository, mViewWithId, handler)
        editorPresenterNewTodo = EditorTodoPresenter(sharedPrefs, repository, mViewWithoutId, handler)
    }

    @Test
    fun createEditorPresenter_withTodoId() {
        editorPresenterWithId.setup()
        Assert.assertNotNull(mViewWithId.resultPresenter)
    }

    @Test
    fun createEditorPresenter_newNote() {
        editorPresenterNewTodo.setup()
        Assert.assertNotNull(mViewWithoutId.resultPresenter)
    }


    @Test
    fun showTodoDetailsWithIdSuccess() = runBlockingTest {
        Mockito.`when`(repository.getData(todo.id!!)).thenReturn(Result.Success(todo))
        editorPresenterWithId.setup()
        Assert.assertNotNull(mViewWithId.resultShowDetails)
    }

    @Test
    fun showTodoDetailsWithIdError() = runBlockingTest {
        Mockito.`when`(repository.getData(todo.id!!)).thenReturn(Result.Error(Exception()))
        editorPresenterWithId.setup()
        Assert.assertNull(mViewWithId.resultShowDetails)
    }

    @Test
    fun showNoteDetailsWithoutId() = runBlockingTest {
        editorPresenterNewTodo.setup()
        Assert.assertEquals(mViewWithoutId.resultSetNoteColor, Color.parseColor("#FF6464"))
        Assert.assertEquals(mViewWithoutId.resultSetTextColor, Color.parseColor("#000000"))
    }

    @Test
    fun saveTodoDetailsWithIdSuccess() = runBlockingTest {
        //  Setup
        Mockito.`when`(repository.getData(todo.id!!)).thenReturn(Result.Success(todo))
        editorPresenterWithId.setup()

        Mockito.`when`(repository.update(todo)).thenReturn(Result.Success())
        editorPresenterWithId.saveTodo(
            todo.title!!,
            todo.todoItems!!,
            todo.color!!,
            todo.textColor!!,
        )
        Assert.assertEquals(mViewWithId.resultMessage, "Todo Updated")
    }
}