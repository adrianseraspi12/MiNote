package com.suzei.minote.ui.list

import com.suzei.minote.BaseUnitTest
import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.local.entity.TodoItem
import com.suzei.minote.data.repository.DataSource
import com.suzei.minote.data.repository.TodoDataSource
import com.suzei.minote.ui.list.todo.ListTodoPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.threeten.bp.OffsetDateTime

@ExperimentalCoroutinesApi
class ListTodoPresenterTest : BaseUnitTest() {
    private lateinit var todoRepository: DataSource<Todo>
    private lateinit var mView: MockListView<Todo>

    private lateinit var listPresenter: ListTodoPresenter
    private lateinit var listOfTodo: List<Todo>

    @Before
    fun setUpListPresenter() = runBlockingTest {
        todoRepository = mock(TodoDataSource::class.java)
        mView = MockListView()
        listPresenter = ListTodoPresenter(todoRepository, mView)
        Dispatchers.setMain(TestCoroutineDispatcher())
        val todoItemList = listOf(
            TodoItem(1, "1", "Sample task", true),
            TodoItem(1, "2", "Sample task 2", false),
            TodoItem(1, "3", "Sample task 3", true)
        )

        listOfTodo = mutableListOf(
            Todo(
                "1",
                "First Note",
                listOf(todoItemList[0]),
                "#FFFFFF",
                "#0d46a0",
                OffsetDateTime.now()
            ),
            Todo(
                "2",
                "First Note",
                listOf(todoItemList[1]),
                "#FFFFFF",
                "#0d46a0",
                OffsetDateTime.now()
            ),
            Todo(
                "3",
                "First Note",
                listOf(todoItemList[2]),
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
        `when`(todoRepository.getListOfData()).thenReturn(Result.Success(listOfTodo))
        listPresenter.setup()
        Assert.assertEquals(listOfTodo, mView.listOfNotes)
    }

    @Test
    fun showListOfNotesError() = runBlockingTest {
        `when`(todoRepository.getListOfData()).thenReturn(Result.Error(Exception()))
        listPresenter.setup()
        Assert.assertNull(mView.listOfNotes)
    }
}