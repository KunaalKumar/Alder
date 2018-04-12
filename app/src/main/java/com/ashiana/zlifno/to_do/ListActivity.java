package com.ashiana.zlifno.to_do;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ashiana.zlifno.to_do.data.Note;

import java.util.ArrayList;

import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;


public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteListAdapter adapter;
    private FABsMenu menuFab;
    private TitleFAB addNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.notes_recycler_view);
        recyclerView.setHasFixedSize(true);

        adapter = new NoteListAdapter(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuFab = findViewById(R.id.menu_fab);
        addNote = findViewById(R.id.add_note);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NoteViewActivity.class);
                startActivity(i);
            }
        });
    }
}
