package com.ashiana.zlifno.alder.activity;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ashiana.zlifno.alder.add.AddTextNoteActivity;
import com.ashiana.zlifno.alder.recycler_view_components.NoteListAdapter;
import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.Note;
import com.ashiana.zlifno.alder.view_model.ListViewModel;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import net.danlew.android.joda.JodaTimeAndroid;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteListAdapter adapter;
    private ListViewModel listViewModel;
    private SpeedDialView speedDialView;
    private int listSize;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Tags for SharedPreferences
    public static String TAG_FINISHED_SPOTLIGHT1 = "FINISHED_SPOTLIGHT1";
    public static String TAG_FINISHED_SPOTLIGHT2 = "FINISHED_SPOTLIGHT2";
    public static String TAG_FINISHED_FINAL_SPOTLIGHT = "FINISHED_FINAL_SPOTLIGHT";

    private final int REQUEST_CAMERA = 1;
    private final int REQUEST_IMAGE = 2;
    public static final int NOTE_VIEW_ACTIVITY_REQUEST_CODE = 1;

    public static Note isNewNote;
    public static FragmentManager fragmentManager;
    // For spotlight
    SimpleTarget fabSpotlight, animSpotlight, fabSpotlight2, fabSpotlight3, noteCardSpotlight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        JodaTimeAndroid.init(this);
        isNewNote = null;
        sharedPreferences = getSharedPreferences("alder_prefs", Context.MODE_PRIVATE);

        recyclerView = findViewById(R.id.notes_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoteListAdapter(this);
        recyclerView.setAdapter(adapter);
        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        setLiveDataObserver();
        startTouchListener();

        fragmentManager = getSupportFragmentManager();
        initSpeedDial();
        View rootView = findViewById(R.id.constraint_layout);

        findViewById(R.id.constraint_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                editor = sharedPreferences.edit();
                initSpotlights();
                if (!sharedPreferences.getBoolean(TAG_FINISHED_SPOTLIGHT1, false)) {

                    // callback when Spotlight ends
                    Spotlight.with(ListActivity.this)
                            .setOverlayColor(ContextCompat.getColor(ListActivity.this,
                                    R.color.background)) // background overlay color
                            .setDuration(1000L) // duration of Spotlight emerging and disappearing in ms
                            .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                            .setTargets(fabSpotlight, animSpotlight, fabSpotlight2) // set targets. see below for more info
                            .setClosedOnTouchedOutside(true) // set if target is closed when touched outside
                            .setOnSpotlightEndedListener(() -> {
                                if (sharedPreferences.getBoolean(TAG_FINISHED_SPOTLIGHT1, true)) {
                                    speedDialView.open();
                                    startSpotlight2();
                                }
                            })
                            .start(); // start Spotlight

                    editor.putBoolean(TAG_FINISHED_SPOTLIGHT1, true);
                    editor.apply();
                }
            }
        });
    }

    private void initSpotlights() {
        fabSpotlight = new SimpleTarget.Builder(ListActivity.this)
                .setPoint(speedDialView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(200f) // radius of the Target
                .setTitle("Fabulous Button") // title
                .setDescription("This is where you'll be able to add notes") // description
                .build();

        animSpotlight = new SimpleTarget.Builder(ListActivity.this)
                .setPoint(findViewById(R.id.animation_view))
                .setRadius(1000f)
                .setTitle("It's empty here")
                .setDescription("Why not add something?")
                .build();

        fabSpotlight2 = new SimpleTarget.Builder(ListActivity.this)
                .setPoint(speedDialView)
                .setRadius(200f)
                .setTitle("Lets add a note")
                .setDescription("Click here to bring add options")
                .build();

        fabSpotlight3 = new SimpleTarget.Builder(ListActivity.this)
                .setPoint(speedDialView)
                .setRadius(200f)
                .setTitle("Click here to add a note")
                .build();

        noteCardSpotlight = new SimpleTarget.Builder(ListActivity.this)
                .setPoint((recyclerView.getX() + recyclerView.getRight()) / 2, (recyclerView.getY() + recyclerView.getBottom() / 3)) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(600f) // radius of the Target
                .setTitle("Congratulations") // title
                .setDescription("You made your first note.\n Now, swipe it to delete it and you're all done") // description
                .build();
    }

    private void setLiveDataObserver() {
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
    }

    private void startSpotlight2() {
        Spotlight.with(ListActivity.this)
                .setOverlayColor(ContextCompat.getColor(ListActivity.this, R.color.background)) // background overlay color
                .setDuration(1000L) // duration of Spotlight emerging and disappearing in ms
                .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                .setTargets(fabSpotlight3) // set targets. see below for more info
                .setClosedOnTouchedOutside(true) // set if target is closed when touched outside
                .setOnSpotlightEndedListener(() -> {
                    editor.putBoolean(TAG_FINISHED_SPOTLIGHT2, true);
                    editor.apply();
                })
                .start(); // start Spotlight
    }

    private void startTouchListener() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP,
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

                        Note noteToDelete = adapter.getNote(viewHolder.getAdapterPosition());

                        listViewModel.deleteNote(noteToDelete);
                        adapter.deleteNote(viewHolder.getAdapterPosition());


                        Snackbar.make(findViewById(R.id.coordinator_layout), "Note deleted", Snackbar.LENGTH_INDEFINITE)
                                .setDuration(5000)
                                .setAction("UNDO", v -> {
                                    listViewModel.insertNote(noteToDelete);
                                    adapter.notifyDataSetChanged();
                                })
                                .show();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void checkScroll() {
        if (!(recyclerView.computeHorizontalScrollRange() > recyclerView.getWidth())) {
            speedDialView.show();
        }
    }

    private void initSpeedDial() {

        speedDialView = findViewById(R.id.speedDial);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_add, R.drawable.baseline_photo_camera_white_24dp)
                        .setLabel("Image note")
                        .setFabBackgroundColor(getResources().getColor(R.color.colorAccentLight))
                        .setLabelBackgroundColor(getResources().getColor(R.color.colorAccentLight))
                        .setLabelColor(Color.WHITE)
                        .create()
        );

        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public void onMainActionSelected() {

                Intent intent = new Intent(ListActivity.this, AddTextNoteActivity.class);

                startActivityForResult(intent, NOTE_VIEW_ACTIVITY_REQUEST_CODE);
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
            }
        });

        speedDialView.setOnActionSelectedListener(speedDialActionItem -> {
            switch (speedDialActionItem.getId()) {
                case R.id.fab_add:
                    requestPerms();
                    return false; // true to keep the Speed Dial open
                default:
                    return false;
            }
        });
    }

    private void requestPerms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted

            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted!
                    openCamera();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showSnackBar("Can't use camera unless permission is granted", R.color.colorAccentDark);
                }
                return;
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        hideKeyboard();

        if (!sharedPreferences.getBoolean(TAG_FINISHED_FINAL_SPOTLIGHT, false)) {
            Spotlight.with(this)
                    .setOverlayColor(ContextCompat.getColor(this, R.color.background)) // background overlay color
                    .setDuration(1000L) // duration of Spotlight emerging and disappearing in ms
                    .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                    .setTargets(noteCardSpotlight) // set targets. see below for more info
                    .setClosedOnTouchedOutside(true) // set if target is closed when touched outside
                    .setOnSpotlightEndedListener(() -> {
                        editor.putBoolean(TAG_FINISHED_FINAL_SPOTLIGHT, true);
                        editor.apply();
                    })
                    .start(); // start Spotlight
        }

        if (requestCode == NOTE_VIEW_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.hasExtra(AddTextNoteActivity.UPDATE_NOTE_EXTRA)) {
                listViewModel.updateNote((Note) data.getSerializableExtra(AddTextNoteActivity.UPDATE_NOTE_EXTRA));
                recyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
                adapter.notifyItemInserted(listSize);
            } else if (data.hasExtra(AddTextNoteActivity.SAVE_NOTE_EXTRA)) {
                Note note = (Note) data.getSerializableExtra(AddTextNoteActivity.SAVE_NOTE_EXTRA);
                isNewNote = note;
                Log.v("Alder", "Inserting note " + note.title);
                note.position = listSize + 1;
                listViewModel.insertNote(note);

                recyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
                adapter.notifyItemInserted(listSize);
            }
        } else if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                showSnackBar("Got image but still need to implement", R.color.colorAccentLight);
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (AddTextNoteActivity.viaSwipe) {
                Log.v("Alder", "Note cancelled");
            } else if (!AddTextNoteActivity.viaBack) {
                showSnackBar("Title can't be empty", android.R.color.holo_red_light);
                AddTextNoteActivity.viaBack = false;
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }
        }

        closeFAB();
    }

    @Override
    public void onBackPressed() {
        // Stop back press if user hasn't finished spotlight
        if (sharedPreferences.getBoolean(TAG_FINISHED_FINAL_SPOTLIGHT, false)) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                String last = getSupportFragmentManager().getBackStackEntryAt(
                        getSupportFragmentManager()
                                .getBackStackEntryCount() - 1)
                        .getName();

                // Coming back from AddTextNote
                if (last.equals("AddTextNote")) {
                    closeFAB();
                    super.onBackPressed();
                }
            } else {
                if (!closeFAB()) {
                    super.onBackPressed();
                }
            }
        }
    }

    // Called on touch
    public static void updateNote(Note note, Context context, View titleView, View timeCreatedView) {

        Intent intent = new Intent(context, AddTextNoteActivity.class);
        intent.putExtra(AddTextNoteActivity.EXTRA_CURRENT_NOTE, note);
        Pair<View, String> p1 = Pair.create(titleView, "title_transition");
        Pair<View, String> p2 = Pair.create(timeCreatedView, "time_transition");

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (Activity) context, p1, p2
        );
        ((Activity) context).startActivityForResult(intent, NOTE_VIEW_ACTIVITY_REQUEST_CODE, options.toBundle());
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void showSnackBar(String test, int color) {

        Snackbar snackbar;
        snackbar = Snackbar.make(this.findViewById(R.id.coordinator_layout), test, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(color));
        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    public boolean closeFAB() {
        if (speedDialView.isOpen()) {
            speedDialView.close();
            return true;
        }
        return false;
    }
}