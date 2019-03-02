package com.suzei.minote.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
class Notes {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int? = null

    var title: String? = null

    var password: String? = null

    var message: String? = null

    @ColumnInfo(name = "text_color")
    var textColor: String? = null

    var color: String? = null

    constructor(title: String, password: String?, message: String, textColor: String, color: String) {
        this.title = title
        this.password = password
        this.message = message
        this.textColor = textColor
        this.color = color
    }

    @Ignore
    constructor(id: Int, title: String, password: String?, message: String, textColor: String, color: String) {
        this.id = id
        this.title = title
        this.password = password
        this.message = message
        this.textColor = textColor
        this.color = color
    }

//    private fun generateId(): Int {
//        val r = Random(System.currentTimeMillis())
//        return 10000 + r.nextInt(20000)
//    }

}
