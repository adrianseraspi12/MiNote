package com.suzei.minote.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.suzei.minote.data.entity.TodoItem

@Dao
interface TodoItemDao {

    @Insert
    fun insertTodoItem(todoItem: TodoItem)

    @Query("SELECT * FROM TODO_ITEM WHERE todoId =:id")
    fun getTodoItems(id: String): List<TodoItem>

}