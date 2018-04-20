package com.ashiana.zlifno.alder.Fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.ashiana.zlifno.alder.NoteListAdapter;
import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.TextNote;
import com.ashiana.zlifno.alder.view_model.ListViewModel;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

public class ListFragment extends Fragment {

    static MainIntents intents;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    // Tags for SharedPreferences
    public static String TAG_FINISHED_SPOTLIGHT1 = "FINISHED_SPOTLIGHT1";
    public static String TAG_FINISHED_SPOTLIGHT2 = "FINISHED_SPOTLIGHT2";
    public static String TAG_FINISHED_FINAL_SPOTLIGHT = "FINISHED_FINAL_SPOTLIGHT";

    public interface MainIntents {
        void newNote();

        void updateNote(TextNote textNote, int position, View v);
    }

    private View rootView;

    private ListViewModel listViewModel;
    private SpeedDialView speedDialView;
    private RecyclerView recyclerView;
    private NoteListAdapter adapter;
    private int listSize;
    public static TextNote isNewNote;

    // For spotlight
    SimpleTarget fabSpotlight, animSpotlight, fabSpotlight2, fabSpotlight3, noteCardSpotlight;

    public ListFragment() {

    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        isNewNote = null;

        recyclerView = rootView.findViewById(R.id.notes_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NoteListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        setLiveDataObserver();
        startTouchListener();

        initSpeedDial();

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                sharedPreferences = getContext().getSharedPreferences("alder_prefs", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                initSpotlights();
                if (!sharedPreferences.getBoolean(TAG_FINISHED_SPOTLIGHT1, false)) {

                    // callback when Spotlight ends
                    Spotlight.with(getActivity())
                            .setOverlayColor(ContextCompat.getColor(getContext(), R.color.background)) // background overlay color
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

        return rootView;
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
                        rootView.findViewById(R.id.animation_view).setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        rootView.findViewById(R.id.animation_view).setVisibility(View.INVISIBLE);
                    }

                    Log.v("Alder", "Updated notes list");
                });
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

                        showSnackBar("TextNote deleted", android.R.color.holo_orange_dark);
                        listViewModel.deleteNote(adapter.getNote(viewHolder.getAdapterPosition()));
                        adapter.deleteNote(viewHolder.getAdapterPosition());

                        checkScroll();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public static void updateNote(TextNote textNote, int position, View v) {
        intents.updateNote(textNote, position, v);
    }

    public void addNote(TextNote textNote) {

        Log.v("Alder", "Changing textNote");
        if (!sharedPreferences.getBoolean(TAG_FINISHED_FINAL_SPOTLIGHT, false)) {
            Spotlight.with(getActivity())
                    .setOverlayColor(ContextCompat.getColor(getContext(), R.color.background)) // background overlay color
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
        isNewNote = textNote;
        Log.v("Adler", "Adding new textNote " + textNote.getTitle());
        listViewModel.insertNote(textNote);

        recyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
        adapter.notifyItemInserted(listSize);
        showSnackBar("New textNote added", R.color.colorAccent);
    }

    public void saveNote(TextNote textNote) {
        listViewModel.updateNote(textNote);
        recyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
        adapter.notifyDataSetChanged();
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
        snackbar = Snackbar.make(rootView.findViewById(R.id.coordinator_layout), test, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(color));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();

    }

    private void initSpeedDial() {

        speedDialView = rootView.findViewById(R.id.speedDial);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_add, R.drawable.ic_arrow_drop_up_white_24dp)
                        .setLabel("Coming soon")
                        .setFabBackgroundColor(getResources().getColor(R.color.colorAccentLight))
                        .setLabelBackgroundColor(getResources().getColor(R.color.colorAccentLight))
                        .setLabelColor(Color.WHITE)
                        .create()
        );

        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public void onMainActionSelected() {

                intents.newNote();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            intents = (MainIntents) context;
        } catch (ClassCastException e) {
            Log.e("Alder", context.toString() + " Must implement MainIntents");
            throw e;
        }
    }

    public boolean closeFAB() {
        if (speedDialView.isOpen()) {
            speedDialView.close();
            return true;
        }
        return false;
    }

    private void initSpotlights() {
        fabSpotlight = new SimpleTarget.Builder(getActivity())
                .setPoint(speedDialView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(200f) // radius of the Target
                .setTitle("Fabulous Button") // title
                .setDescription("This is where you'll be able to add notes") // description
                .build();

        animSpotlight = new SimpleTarget.Builder(getActivity())
                .setPoint(getActivity().findViewById(R.id.animation_view))
                .setRadius(1000f)
                .setTitle("It's empty here")
                .setDescription("Why not add something?")
                .build();

        fabSpotlight2 = new SimpleTarget.Builder(getActivity())
                .setPoint(speedDialView)
                .setRadius(200f)
                .setTitle("Lets add a note")
                .setDescription("Click here to bring add options")
                .build();

        fabSpotlight3 = new SimpleTarget.Builder(getActivity())
                .setPoint(speedDialView)
                .setRadius(200f)
                .setTitle("Click here to add a note")
                .build();

        noteCardSpotlight = new SimpleTarget.Builder(getActivity())
                .setPoint((recyclerView.getX() + recyclerView.getRight()) / 2, (recyclerView.getY() + recyclerView.getBottom() / 3)) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(600f) // radius of the Target
                .setTitle("Congratulations") // title
                .setDescription("You made your first note.\n Now, swipe it to delete it and you're all done") // description
                .build();
    }

    private void startSpotlight2() {
        Spotlight.with(getActivity())
                .setOverlayColor(ContextCompat.getColor(getContext(), R.color.background)) // background overlay color
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
}