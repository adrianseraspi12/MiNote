package com.suzei.minote.data;

import java.util.Random;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Notes {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    private int id;

    private String title;

    private String password;

    private String message;

    @ColumnInfo(name = "text_color")
    private String textColor;

    private String color;

    public Notes(String title, String password, String message, String textColor, String color) {
        this.id = generateId();
        this.title = title;
        this.password = password;
        this.message = message;
        this.textColor = textColor;
        this.color = color;
    }

    public Notes(int id, String title, String password, String message, String textColor, String color) {
        this.id = id;
        this.title = title;
        this.password = password;
        this.message = message;
        this.textColor = textColor;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    private int generateId() {
        Random r = new Random(System.currentTimeMillis());
        return 10000 + r.nextInt(20000);
    }

}
