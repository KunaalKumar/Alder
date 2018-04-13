package com.ashiana.zlifno.alder.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

// Defines columns to add to database
@Entity(tableName = "note_table")
public class Note implements Serializable {

    public Note(@NonNull String title, String content, String timeCreated) {
        this.title = title;
        this.content = content;
        this.timeCreated = timeCreated;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @NonNull
    private String title;

    private String content;

    private String timeCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }
}
