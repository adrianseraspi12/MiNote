package com.suzei.minote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.utils.LogMe

@Dao
abstract class TodoDao {

    @Insert
    abstract fun insertTodo(todo: Todo)

    @Insert
    abstract fun insertTodoItemList(todoItems: List<TodoItem>)

    @Query("SELECT * FROM TODO")
    abstract fun getAllTodo(): List<Todo>

    @Query("SELECT * FROM TODO WHERE _id =:id")
    abstract fun getTodo(id: Int): Todo

    @Query("SELECT * FROM TODO_ITEM WHERE todoId =:id")
    abstract fun getTodoItems(id: Int): List<TodoItem>

    fun getAllTodoWithTasks(): List<Todo> {
        val listOfTodo = getAllTodo()

        for (todo in listOfTodo) {
            val id = todo.id
            todo.todoItems = id?.let { getAllTasksById(it) }
        }

        return listOfTodo
    }

    fun getTodoWithTasksById(id: Int): Todo {
        val todo = getTodo(id)
        todo.todoItems = getAllTasksById(id)
        return todo
    }

    fun insertTodoWithTasks(todo: Todo) {
        val todoItems = todo.todoItems

        if (todoItems != null) {
            LogMe.info("TodoDao =  Save")

            for (item in todoItems) {
                item.todoId = todo.id
            }

            insertTodoItemList(todoItems)
            insertTodo(todo)
        }

    }

    private fun getAllTasksById(id: Int): List<TodoItem> {
        return getTodoItems(id)
    }

}