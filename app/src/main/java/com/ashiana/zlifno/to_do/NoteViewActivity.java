package com.ashiana.zlifno.to_do;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rengwuxian.materialedittext.MaterialEditText;

public class NoteViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_view_layout);

        getSupportActionBar().setElevation(4);

        MaterialEditText noteContent = findViewById(R.id.note_content);
        noteContent.setMetTextColor(Color.WHITE);
    }
}
