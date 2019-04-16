package com.suzei.minote.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
class Todo() {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int? = null

    var title: String? = null

    @Ignore
    var todoItems: List<TodoItem>? = null

    @ColumnInfo(name = "text_color")
    var textColor: String? = null

    var color: String? = null

    constructor(title: String, todoItem: List<TodoItem>, textColor: String, color: String) : this() {
        this.title = title
        this.todoItems = todoItem
        this.textColor = textColor
        this.color = color
    }

    @Ignore
    constructor(id: Int, title: String, todoItem: List<TodoItem>, textColor: String,color: String) : this() {
        this.id = id
        this.title = title
        this.todoItems = todoItem
        this.textColor = textColor
        this.color = color
    }

}