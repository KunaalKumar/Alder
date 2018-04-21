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
    public int id;

    @NonNull
    public String title;

    public String content;

    public String timeCreated;

    public String timeEdited;

    public int position;

    public String imagePath;

}
