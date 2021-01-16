package com.suzei.minote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.suzei.minote.data.entity.TodoItem

@Dao
interface TodoItemDao {

    @Insert
    fun insertTodoItem(todoItem: TodoItem)

    @Query("DELETE FROM todo_item WHERE todoId =:id")
    fun deleteAllTodoItem(id: String)

    @Query("SELECT * FROM TODO_ITEM WHERE todoId =:id")
    fun getTodoItems(id: String): List<TodoItem>

}