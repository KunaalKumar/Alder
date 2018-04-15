package com.ashiana.zlifno.alder;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

import com.ashiana.zlifno.alder.view_model.ListViewModel;
import com.ashiana.zlifno.alder.data.Note;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.List;

import maes.tech.intentanim.CustomIntent;


public class MainActivity extends AppCompatActivity {

    public static final int NOTE_VIEW_ACTIVITY_REQUEST_CODE = 1;

    private ListViewModel listViewModel;
    private SpeedDialView speedDialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        RecyclerView recyclerView = findViewById(R.id.notes_recycler_view);
        recyclerView.setHasFixedSize(true);

        final NoteListAdapter adapter = new NoteListAdapter(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DragScrollBar materialScrollBar = new DragScrollBar(this, recyclerView, true);

        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        initSpeedDial();
//         Observer for Live Data
        listViewModel.getNotesList().

                observe(this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        Log.v("APPD", "Main: Item count is " + notes.size());

                        if (listViewModel.inProgress) {
                            return;
                        }

                        adapter.setNotes(notes);
                        for (int i = 0; i < notes.size(); i++) {
                            Log.v("POS1", String.valueOf(notes.get(i).getPosition()));
                        }
                        Log.v("APPD", "Updated notes list");
                    }
                });


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            int dragFrom = -1;
            int dragTo = -1;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Log.d("Alder", "Dragged");

                if (dragFrom == -1) {
                    dragFrom = fromPosition;
                }
                dragTo = toPosition;

                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                return true;
            }

            // Called on drop
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    listViewModel.moveNote(adapter.getNote(dragFrom), adapter.getNote(dragTo), adapter);
                    adapter.notifyItemMoved(dragFrom, dragTo);
                }
                dragTo = dragFrom = -1;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                listViewModel.deleteNote(adapter.getNote(viewHolder.getAdapterPosition()));
                adapter.deleteNote(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        CustomIntent.customType(MainActivity.this, "rotateout-to-rotatein");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

    private void showSnackBar(String test) {
        android.support.design.widget.Snackbar
                .make(findViewById(R.id.notes_recycler_view), test, android.support.design.widget.Snackbar.LENGTH_LONG).show();
    }

    private void initSpeedDial() {

        speedDialView = findViewById(R.id.speedDial);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_add, R.drawable.ic_arrow_drop_down_white_24dp)
                        .setLabel("Coming soon")
                        .setFabBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setLabelBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setLabelColor(Color.WHITE)
                        .create()
        );

        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public void onMainActionSelected() {
                Intent i = new Intent(getApplicationContext(), AddTextNoteActivity.class);
                Log.v("Alder", "Started Add Note Activity");
                startActivityForResult(i, NOTE_VIEW_ACTIVITY_REQUEST_CODE);
                if (speedDialView.isOpen()) {
                    speedDialView.close();
                }
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
            }
        });

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener()

        {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.fab_add:
                        showSnackBar("More coming soon!");
                        speedDialView.close();
                        return false; // true to keep the Speed Dial open
                    default:
                        return false;
                }
            }
        });
    }
}
