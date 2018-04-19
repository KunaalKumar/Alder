package com.ashiana.zlifno.alder.Fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashiana.zlifno.alder.NoteListAdapter;
import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.TextNote;
import com.ashiana.zlifno.alder.view_model.ListViewModel;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;


public class ListFragment extends Fragment {

    static MainIntents intents;

    public interface MainIntents {
        void newNote();

        void updateNote(TextNote textNote);
    }

    private View rootView;

    private ListViewModel listViewModel;
    private SpeedDialView speedDialView;
    private RecyclerView recyclerView;
    private NoteListAdapter adapter;
    private int listSize;
    public static String isNewTitle;
    public static String isNewTime;

    public ListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        isNewTitle = null;
        isNewTime = null;

        recyclerView = rootView.findViewById(R.id.notes_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NoteListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        setLiveDataObserver();
        startTouchListener();

        initSpeedDial();

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

                        checkScroll();

                        showSnackBar("TextNote deleted", android.R.color.holo_orange_dark);
                        listViewModel.deleteNote(adapter.getNote(viewHolder.getAdapterPosition()));
                        adapter.deleteNote(viewHolder.getAdapterPosition());
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public static void updateNote(TextNote textNote) {
        intents.updateNote(textNote);
    }

    public void addNote(TextNote textNote) {
        Log.v("Alder", "Changing textNote");
        isNewTitle = textNote.getTitle();
        isNewTime = textNote.getTimeCreated();
        Log.v("Adler", "Adding new textNote " + isNewTitle);
        listViewModel.insertNote(textNote);

        recyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
        adapter.notifyItemInserted(listSize);
        showSnackBar("New textNote added", R.color.colorAccent);
    }

    public void saveNote(TextNote textNote) {
        listViewModel.updateNote(textNote);
        recyclerView.smoothScrollToPosition(View.FOCUS_DOWN);
        adapter.notifyItemChanged(listSize);
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
//                Intent intent = new Intent(getActivity().getBaseContext(), AddTextNoteFragment.class);
//                startActivityForResult(intent, NOTE_VIEW_ACTIVITY_REQUEST_CODE);
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

}