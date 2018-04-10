package com.ashiana.zlifno.to_do;

import java.util.Calendar;
import java.util.Date;

public class Note {

    private String title;
    private String content;
    private Date timeCreated;

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        timeCreated = Calendar.getInstance().getTime();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
