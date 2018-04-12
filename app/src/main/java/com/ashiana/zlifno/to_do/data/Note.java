package com.ashiana.zlifno.to_do.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

@Entity
public class Note {

    @PrimaryKey
    private String id;

    @NonNull
    private String title;

    private String content;

    private Date timeCreated;
}
