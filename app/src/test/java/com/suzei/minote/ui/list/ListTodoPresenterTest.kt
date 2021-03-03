package com.suzei.minote.ui.list

import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.data.repository.Repository
import com.suzei.minote.data.repository.TodoRepository
import com.suzei.minote.BaseUnitTest
import com.suzei.minote.ui.list.todo.ListTodoFragment
import com.suzei.minote.ui.list.todo.ListTodoPresenter
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.threeten.bp.OffsetDateTime

class ListTodoPresenterTest : BaseUnitTest() {
    private lateinit var repository: Repository<Todo>
    private lateinit var mView: ListContract.View<Todo>
    private lateinit var listTodoCaptor: ArgumentCaptor<Repository.ListListener<Todo>>

    private lateinit var listPresenter: ListTodoPresenter
    private lateinit var listOTodo: MutableList<Todo>

    private val todo = Todo("1",
            "First Note",
            listOf(TodoItem(1, "1", "Sample task", true)),
            "#FFFFFF",
            "#0d46a0",
            OffsetDateTime.now()
    )

    @Before
    fun setUpListPresenter() {
        repository = Mockito.mock(TodoRepository::class.java)
        mView = Mockito.mock(ListTodoFragment::class.java)
        listPresenter = ListTodoPresenter(repository, mView)
        listTodoCaptor = argumentCaptor()

        val todoItemList = listOf(
                TodoItem(1, "1", "Sample task", true),
                TodoItem(1, "2", "Sample task 2", false),
                TodoItem(1, "3", "Sample task 3", true),
        )
        listOTodo = mutableListOf(
                Todo("1",
                        "First Note",
                        listOf(todoItemList[0]),
                        "#FFFFFF",
                        "#0d46a0",
                        OffsetDateTime.now()
                ),
                Todo("2",
                        "First Note",
                        listOf(todoItemList[1]),
                        "#FFFFFF",
                        "#0d46a0",
                        OffsetDateTime.now()
                ),
                Todo("3",
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
        Mockito.verify(mView).setPresenter(listPresenter)
    }

    @Test
    fun showListOfNotes() {
        //  Listeners are fired
        listPresenter.setup()
        Mockito.verify(repository).getListOfData(capture(listTodoCaptor))

        //  Notes shown in UI
        listTodoCaptor.value.onDataAvailable(listOTodo)
        val showTodoArgumentCaptor = argumentCaptor<MutableList<Todo>>()
        Mockito.verify(mView).showListOfNotes(capture(showTodoArgumentCaptor))
        Assert.assertEquals(3, showTodoArgumentCaptor.value.size.toLong())
    }

    @Test
    fun deleteNote() {
        listPresenter.delete(todo)
        Mockito.verify(repository).delete(todo)
    }
}