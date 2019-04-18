package com.suzei.minote.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "notes")
class Notes {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    @NonNull
    var id: String? = null

    var title: String? = null

    var password: String? = null

    var message: String? = null

    @ColumnInfo(name = "text_color")
    var textColor: String? = null

    var color: String? = null

    @ColumnInfo(name = "created_date")
    var createdDate: OffsetDateTime? = null

    constructor(title: String,
                password: String?,
                message: String,
                textColor: String,
                color: String) {

        this.id = UUID.randomUUID().toString()
        this.title = title
        this.password = password
        this.message = message
        this.textColor = textColor
        this.color = color
        this.createdDate = OffsetDateTime.now()

    }

    @Ignore
    constructor(id: String,
                title: String,
                password: String?,
                message: String,
                textColor: String,
                color: String,
                createdDate: OffsetDateTime) {
        this.id = id
        this.title = title
        this.password = password
        this.message = message
        this.textColor = textColor
        this.color = color
        this.createdDate = createdDate
    }

}
