package com.ashiana.zlifno.alder;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ashiana.zlifno.alder.view_model.ListViewModel;
import com.ashiana.zlifno.alder.data.Note;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.turingtechnologies.materialscrollbar.MaterialScrollBar;

import maes.tech.intentanim.CustomIntent;


public class MainActivity extends AppCompatActivity {

    public static final int NOTE_VIEW_ACTIVITY_REQUEST_CODE = 1;

    private ListViewModel listViewModel;
    private SpeedDialView speedDialView;
    private RecyclerView recyclerView;
    private MaterialScrollBar scrollBar;
    private NoteListAdapter adapter;
    private int listSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.notes_recycler_view);
        recyclerView.setHasFixedSize(true);

        scrollBar = findViewById(R.id.dragScrollBar);

        adapter = new NoteListAdapter(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        initSpeedDial();
//         Observer for Live Data
        listViewModel.getNotesList().

                observe(this, notes -> {
                    Log.v("Alder", "Main: Item count is " + notes.size());

                    // Wait for the list to be updated completely
                    if (listViewModel.inProgress) {
                        Log.v("Alder", "Still updating list");
                        return;
                    }

                    adapter.setNotes(notes);
                    listSize = notes.size();

                    if (notes.size() == 0) {
                        recyclerView.setVisibility(View.INVISIBLE);
                        findViewById(R.id.animation_view).setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        findViewById(R.id.animation_view).setVisibility(View.INVISIBLE);
                    }

                    Log.v("Alder", "Updated notes list");
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

        Log.v("Alder", "Got intent ! " + requestCode);

        if (requestCode == NOTE_VIEW_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Note note = (Note) data.getSerializableExtra(AddTextNoteActivity.SAVE_NOTE_EXTRA);
            Log.v("Alder", "Inserting note " + note.getTitle());
            listViewModel.insertNote(note);

            recyclerView.smoothScrollToPosition(recyclerView.FOCUS_DOWN);
            scrollBar.computeScroll();
            adapter.notifyItemInserted(listSize);

        } else if (resultCode == RESULT_CANCELED) {
        } else {
            showSnackBar("Title can't be empty");
        }
    }

    // Helper to print a snackbar, just pass in the string
    private void showSnackBar(String test) {
        Snackbar snackbar = Snackbar.make(recyclerView, test, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(recyclerView.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    private void initSpeedDial() {

        speedDialView = findViewById(R.id.speedDial);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_add, R.drawable.ic_arrow_drop_up_white_24dp)
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

//                        ConstraintSet constraintSet1 = new ConstraintSet();
//                        ConstraintSet constraintSet2 = new ConstraintSet();
//
//                        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
//                        constraintSet2.clone(MainActivity.this, R.layout.activity_list_t);
//                        constraintSet1.clone(constraintLayout);
//
//                        TransitionManager.beginDelayedTransition(constraintLayout);
//                        constraintSet2.applyTo(constraintLayout);

                        speedDialView.close();
                        return false; // true to keep the Speed Dial open
                    default:
                        return false;
                }
            }
        });
    }
}