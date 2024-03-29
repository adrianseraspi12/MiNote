package com.suzei.minote.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "todo_item")
class TodoItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int? = null

    var todoId: String? = null

    var task: String? = null

    var completed: Boolean? = null

    constructor(task: String, completed: Boolean) {
        this.task = task
        this.completed = completed
    }

    @Ignore
    constructor(id: Int, todoId: String, task: String, completed: Boolean) {
        this.id = id
        this.todoId = todoId
        this.task = task
        this.completed = completed
    }

}