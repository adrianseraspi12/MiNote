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

    @Query("SELECT * FROM TODO")
    abstract fun getAllTodo(): List<Todo>

    @Query("SELECT * FROM TODO WHERE _id =:id")
    abstract fun getTodo(id: String): Todo

    fun getAllTodoWithTasks(todoItemDao: TodoItemDao): List<Todo> {
        val listOfTodo = getAllTodo()

        for (todo in listOfTodo) {
            val id = todo.id
            todo.todoItems = id?.let { getAllTasksById(it, todoItemDao) }
        }

        return listOfTodo
    }

    fun getTodoWithTasksById(id: String, todoItemDao: TodoItemDao): Todo {
        val todo = getTodo(id)
        todo.todoItems = getAllTasksById(id, todoItemDao)
        return todo
    }

    fun insertTodoWithTasks(todo: Todo, todoItemDao: TodoItemDao) {
        val todoItems = todo.todoItems

        if (todoItems != null) {
            insertTodo(todo)
            LogMe.info("TodoDao =  Save")

            for (item in todoItems) {
                LogMe.info("TodoItemDao =  Save")
                item.todoId = todo.id

                //  Not saving because todoid doesnt have an id
                //  generate an id
                //  add date attribute to tod0

                LogMe.info("TODO ID = ${todo.id}")
                LogMe.info("TODO ITEM ID = ${item.todoId}")
                todoItemDao.insertTodoItem(item)
            }

        }

    }

    private fun getAllTasksById(id: String, todoItemDao: TodoItemDao): List<TodoItem> {
        LogMe.info("TODO ID = $id")

        val todoItem = todoItemDao.getTodoItems(id)

        LogMe.info("TODO COUNT = ${todoItem.size}")

        for (todo in todoItem) {
            LogMe.info("TODO = ${todo.task}")
        }

        return todoItem
    }

}