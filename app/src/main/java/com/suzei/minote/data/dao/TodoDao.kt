package com.suzei.minote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.data.entity.TodoItem
import com.suzei.minote.utils.LogMe

@Dao
abstract class TodoDao {

    @Insert
    abstract fun insertTodo(todo: Todo)

    @Update
    abstract fun updateTodo(todo: Todo)

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

        insertTodo(todo)
        LogMe.info("TodoDao =  Save")

        insertTask(todo.id!!, todoItems, todoItemDao)
    }

    //  Delete all todoitem then add all task
    fun updateTodoWithTasks(todo: Todo, todoItemDao: TodoItemDao) {
        val todoItems = todo.todoItems

        updateTodo(todo)
        todoItemDao.deleteAllTodoItem(todo.id!!)

        LogMe.info("TodoDao = Updating")

        insertTask(todo.id!!, todoItems, todoItemDao)
    }

    private fun insertTask(id: String, todoItems: List<TodoItem>?, todoItemDao: TodoItemDao) {
        todoItems?.let {

            for (item in it) {
                item.todoId = id
                LogMe.info("TodoItemDao = Updating")

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