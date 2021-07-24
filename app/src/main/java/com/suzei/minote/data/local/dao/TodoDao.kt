package com.suzei.minote.data.local.dao

import androidx.room.*
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.local.entity.TodoItem
import com.suzei.minote.utils.LogMe

@Dao
abstract class TodoDao {

    @Insert
    abstract fun insertTodo(todo: Todo)

    @Delete
    abstract fun deleteTodo(todo: Todo)

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

    fun deleteTodoWithTasks(todo: Todo, todoItemDao: TodoItemDao) {
        val todoId = todo.id
        deleteTodo(todo)
        todoItemDao.deleteAllTodoItem(todoId!!)
        LogMe.info("TodoDao = Delete")
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
        return todoItemDao.getTodoItems(id)
    }

}