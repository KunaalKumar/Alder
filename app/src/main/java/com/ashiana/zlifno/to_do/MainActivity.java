package com.ashiana.zlifno.to_do;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ashiana.zlifno.to_do.data.NoteContract;
import com.ashiana.zlifno.to_do.data.NoteDBHelper;

import java.util.ArrayList;

import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FABsMenu menuFab;
    private TitleFAB addNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Note> notes = new ArrayList<>();

        recyclerView = findViewById(R.id.notes_recycler_view);
        recyclerView.setHasFixedSize(true);
//
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NotesAdapter(notes);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        menuFab = findViewById(R.id.menu_fab);
        addNote = findViewById(R.id.add_note);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(i);
            }
        });
    }
}
