package com.suzei.minote.ui.editor

import android.graphics.Color
import com.suzei.minote.BaseUnitTest
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.data.repository.TodoRepository
import com.suzei.minote.ui.editor.todo.EditorTodoContract
import com.suzei.minote.ui.editor.todo.EditorTodoFragment
import com.suzei.minote.ui.editor.todo.EditorTodoPresenter
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.threeten.bp.OffsetDateTime

class EditorTodoPresenterTest : BaseUnitTest() {

    private lateinit var editorPresenterWithId: EditorTodoPresenter
    private lateinit var editorPresenterNewTodo: EditorTodoPresenter

    private lateinit var mView: EditorTodoContract.View
    private lateinit var repository: Repository<Todo>
    private lateinit var todoListenerArgumentCaptor: ArgumentCaptor<Repository.Listener<Todo>>

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
        todoListenerArgumentCaptor = argumentCaptor()
        repository = Mockito.mock(TodoRepository::class.java)
        mView = Mockito.mock(EditorTodoFragment::class.java)
        editorPresenterWithId = EditorTodoPresenter(todo.id!!, repository, mView)
        editorPresenterNewTodo = EditorTodoPresenter(repository, mView)
        editorPresenterWithId.setup()
        editorPresenterNewTodo.setup()
    }

    @Test
    fun createEditorPresenter_withTodoId() {
        Mockito.verify(mView).setPresenter(editorPresenterWithId)
    }

    @Test
    fun createEditorPresenter_newNote() {
        Mockito.verify(mView).setPresenter(editorPresenterNewTodo)
    }

    @Test
    fun showTodoDetailsWithId() {
        Mockito.verify(repository).getData(safeEq(todo.id!!), capture(todoListenerArgumentCaptor))

        //  Note details shown in UI
        todoListenerArgumentCaptor.value.onDataAvailable(todo)
        val todoArgumentCaptor: ArgumentCaptor<Todo> = argumentCaptor()
        Mockito.verify(mView).showTodoDetails(capture(todoArgumentCaptor))
    }

    @Test
    fun showNewNote() {
        //  Default color for new note
        val noteColor = "#FF6464"
        val textColor = "#000000"

        Mockito.verify(mView).setTextColor(Color.parseColor(noteColor))
        Mockito.verify(mView).setNoteColor(Color.parseColor(textColor))
    }
}