package com.ashiana.zlifno.alder.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

// Defines columns to add to database
@Entity(tableName = "note_table")
public class Note implements Serializable {

    public static final int NOTE_TYPE_TEXT = 1;
    public static final int NOTE_TYPE_IMAGE = 2;

    public Note(@NonNull String title, @NonNull int noteType, String content, String timeCreated) {
        this.title = title;
        this.content = content;
        this.timeCreated = timeCreated;
        this.noteType = noteType;
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

    public int noteType;

}
