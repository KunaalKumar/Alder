package com.ashiana.zlifno.alder.Activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ashiana.zlifno.alder.NoteListAdapter;
import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.view_model.ListViewModel;
import com.ashiana.zlifno.alder.data.Note;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;


public class MainActivity extends AppCompatActivity {

    public static final int NOTE_VIEW_ACTIVITY_REQUEST_CODE = 1;

    private ListViewModel listViewModel;
    private SpeedDialView speedDialView;
    private RecyclerView recyclerView;
    private NoteListAdapter adapter;
    private int listSize;
    public static String isNewTitle;
    public static String isNewTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_list);

        isNewTitle = null;
        isNewTime = null;

        recyclerView = findViewById(R.id.notes_recycler_view);
        recyclerView.setHasFixedSize(true);
//        scrollBar = findViewById(R.id.dragScrollBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoteListAdapter(this);
        recyclerView.setAdapter(adapter);
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

                checkScroll();

                showSnackBar("Note deleted", android.R.color.holo_orange_dark);
                listViewModel.deleteNote(adapter.getNote(viewHolder.getAdapterPosition()));
                adapter.deleteNote(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // TODO add method for transition
    public static void updateNote(View view, Note note, Context context) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, view, "transition");

        Intent intent = new Intent(context, AddTextNoteActivity.class);
        intent.putExtra(AddTextNoteActivity.EXTRA_CURRENT_NOTE, note);
        intent.putExtra(AddTextNoteActivity.EXTRA_CIRCULAR_REVEAL_X, view.getRight());
        intent.putExtra(AddTextNoteActivity.EXTRA_CIRCULAR_REVEAL_Y, view.getBottom());

//        ((Activity) context).startActivityForResult(intent, NOTE_VIEW_ACTIVITY_REQUEST_CODE);
        ActivityCompat.startActivityForResult((Activity) context, intent, NOTE_VIEW_ACTIVITY_REQUEST_CODE, options.toBundle());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("Alder", "Got intent ! " + requestCode);

        if (requestCode == NOTE_VIEW_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.hasExtra(AddTextNoteActivity.UPDATE_NOTE_EXTRA)) {
                listViewModel.updateNote((Note) data.getSerializableExtra(AddTextNoteActivity.UPDATE_NOTE_EXTRA));
                recyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
                adapter.notifyItemInserted(listSize);
                showSnackBar("Note updated", R.color.colorAccent);
            } else if (data.hasExtra(AddTextNoteActivity.SAVE_NOTE_EXTRA)) {
                Note note = (Note) data.getSerializableExtra(AddTextNoteActivity.SAVE_NOTE_EXTRA);
                isNewTitle = note.getTitle();
                isNewTime = note.getTimeCreated();
                Log.v("Alder", "Inserting note " + note.getTitle());
                listViewModel.insertNote(note);

                recyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
                adapter.notifyItemInserted(listSize);
                showSnackBar("Note made", R.color.colorAccent);
            }

        } else if (resultCode == RESULT_CANCELED) {
            if (!AddTextNoteActivity.viaBack) {
                showSnackBar("Title can't be empty", android.R.color.holo_red_light);
                AddTextNoteActivity.viaBack = false;
            }
        }
        // Close fab after activity return
        if (speedDialView.isOpen()) {
            speedDialView.close();
        }
    }

    public void checkScroll() {
        if (!(recyclerView.computeHorizontalScrollRange() > recyclerView.getWidth())) {
            speedDialView.show();
        }
    }

    // Helper to print a snackbar, just pass in the string and background color
    private void showSnackBar(String test, int color) {
//        CafeBar.make(findViewById(R.id.coordinator_layout), test, CafeBar.Duration.MEDIUM).show();

        Snackbar snackbar;
        snackbar = Snackbar.make(findViewById(R.id.coordinator_layout), test, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(color));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
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
                presentActivity(findViewById(R.id.constraint_layout));
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
            }
        });

        speedDialView.setOnActionSelectedListener(speedDialActionItem -> {
            switch (speedDialActionItem.getId()) {
                case R.id.fab_add:
                    showSnackBar("More coming soon!", R.color.colorPrimaryDark);

                    speedDialView.close();
                    return false; // true to keep the Speed Dial open
                default:
                    return false;
            }
        });
    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");
        int revealX = view.getRight();
        int revealY = view.getBottom();

        Intent intent = new Intent(this, AddTextNoteActivity.class);
        intent.putExtra(AddTextNoteActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(AddTextNoteActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);
//        startActivityForResult(intent, NOTE_VIEW_ACTIVITY_REQUEST_CODE);

        ActivityCompat.startActivityForResult(this, intent, NOTE_VIEW_ACTIVITY_REQUEST_CODE, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        if (speedDialView.isOpen()) {
            speedDialView.close();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}