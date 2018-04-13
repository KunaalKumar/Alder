package com.ashiana.zlifno.to_do;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ashiana.zlifno.to_do.data.Note;
import com.ashiana.zlifno.to_do.view_model.ListViewModel;

import java.util.List;

import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;
import maes.tech.intentanim.CustomIntent;


public class MainActivity extends AppCompatActivity {

    public static final int NOTE_VIEW_ACTIVITY_REQUEST_CODE = 1;

    private TitleFAB addNote;
    private ListViewModel listViewModel;
    private FABsMenu fabsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recyclerView = findViewById(R.id.notes_recycler_view);
        recyclerView.setHasFixedSize(true);

        fabsMenu = findViewById(R.id.menu_fab);
        fabsMenu.attachToRecyclerView(recyclerView);

        final NoteListAdapter adapter = new NoteListAdapter(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);

//         Observer for Live Data
        listViewModel.getNotesList().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                Log.v("APPD", "Main: Item count is " + notes.size());
                adapter.setNotes(notes);
                for (int i = 0; i < notes.size(); i++) {
                    Log.v("APPD", notes.get(i).getTitle());
                }
                Log.v("APPD", "Updated notes list");
            }
        });

        addNote = findViewById(R.id.add_note);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddTextNoteActivity.class);
                Log.v("APPD", "Started Add Note Activity");
                startActivityForResult(i, NOTE_VIEW_ACTIVITY_REQUEST_CODE);
            }
        });

        CustomIntent.customType(MainActivity.this, "rotateout-to-rotatein");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fabsMenu.collapse();

        Log.v("APPD", "Got intent ! " + requestCode);

        if (requestCode == NOTE_VIEW_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Note note = (Note) data.getSerializableExtra(AddTextNoteActivity.SAVE_NOTE_EXTRA);
            Log.v("APPD", "Inserting note " + note.getTitle());
            listViewModel.insertNote(note);
        } else if (resultCode == RESULT_CANCELED) {
        } else {
            Toast.makeText(getApplicationContext(), "Title can't be empty", Toast.LENGTH_LONG).show();
        }
    }
}
