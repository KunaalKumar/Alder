package com.ashiana.zlifno.alder.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.Note;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;
import com.victorminerva.widget.edittext.AutofitEdittext;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTextNoteFragment extends Fragment {

    private ChangeNoteIntent changeNoteIntent;

    public interface ChangeNoteIntent {
        void addNote(Note note);

        void saveNote(Note note);

        void titleEmpty();
    }

    View rootView;

    private AutofitEdittext titleEditText;
    private EditText noteContentEditText;
    private SpeedDialView speedDialView;
    private TextView noteTimeTextView;
    private Note current;
    public static boolean viaBack;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SimpleTarget titleSpotlight, noteContentSpotlight, noteTimeSpotlight, saveFabSpotlight, titleSpotlight2;
    public static String TAG_FINISHED_ADD_NOTE_SPOTLIGHT = "FINISHED_ADD_NOTE_SPOTLIGHT";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_note, container, false);
        initSpeedDial();

        viaBack = false;

        titleEditText = rootView.findViewById(R.id.note_title);
        noteTimeTextView = rootView.findViewById(R.id.note_time);
        noteContentEditText = rootView.findViewById(R.id.note_content);

        Bundle args = getArguments();
        if (args != null) {
            Note note = (Note) args.getSerializable("current");
            current = note;
        }

        if (current != null) {
            titleEditText.setText(current.title);
            noteContentEditText.setText(current.content);
            noteTimeTextView.setText(current.timeCreated);
        } else {
            noteTimeTextView.setText(getCurrentDateTime());
        }

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                sharedPreferences = getContext().getSharedPreferences("alder_prefs", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                initSpotlights();
                if (!sharedPreferences.getBoolean(TAG_FINISHED_ADD_NOTE_SPOTLIGHT, false)) {

                    // callback when Spotlight ends
                    Spotlight.with(getActivity())
                            .setOverlayColor(ContextCompat.getColor(getContext(), R.color.background)) // background overlay color
                            .setDuration(1000L) // duration of Spotlight emerging and disappearing in ms
                            .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                            .setTargets(titleSpotlight, noteContentSpotlight, noteTimeSpotlight, saveFabSpotlight, titleSpotlight2)
                            .setClosedOnTouchedOutside(true) // set if target is closed when touched outside
                            .setOnSpotlightEndedListener(() -> {
                                if (sharedPreferences.getBoolean(TAG_FINISHED_ADD_NOTE_SPOTLIGHT, true)) {
                                    titleEditText.setFocusableInTouchMode(true);
                                    titleEditText.requestFocus();
                                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                                            .showSoftInput(titleEditText, InputMethodManager.SHOW_FORCED);
                                }
                            })
                            .start(); // start Spotlight

                    editor.putBoolean(TAG_FINISHED_ADD_NOTE_SPOTLIGHT, true);
                    editor.apply();
                }
            }
        });

        return rootView;
    }

    public static AddTextNoteFragment newInstance() {
        return new AddTextNoteFragment();
    }

    private void initSpotlights() {
        titleSpotlight = new SimpleTarget.Builder(getActivity())
                .setPoint(titleEditText) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(400f) // radius of the Target
                .setTitle("Note Title") // title
                .setDescription("Enter a title for your first note") // description
                .build();
        noteContentSpotlight = new SimpleTarget.Builder(getActivity())
                .setPoint(noteContentEditText) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(1000f) // radius of the Target
                .setTitle("Note Content") // title
                .setDescription("This is where you can add the content for your note") // description
                .build();
        noteTimeSpotlight = new SimpleTarget.Builder(getActivity())
                .setPoint(noteTimeTextView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(500f) // radius of the Target
                .setTitle("Time Created") // title
                .setDescription("Here you can see the time your note was created") // description
                .build();
        saveFabSpotlight = new SimpleTarget.Builder(getActivity())
                .setPoint(speedDialView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(200f) // radius of the Target
                .setTitle("Save Button") // title
                .setDescription("Click here to save your note") // description
                .build();
        titleSpotlight2 = new SimpleTarget.Builder(getActivity())
                .setPoint(titleEditText) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(400) // radius of the Target
                .setTitle("Alright") // title
                .setDescription("Let's make your first note, enter the title and click the save button") // description
                .build();
    }

    private void initSpeedDial() {

        speedDialView = rootView.findViewById(R.id.speedDialAddMenu);

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.save_note, R.drawable.ic_arrow_drop_up_white_24dp)
                        .setLabel("More coming soon!")
                        .setFabBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setLabelBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setLabelColor(Color.WHITE)
                        .create()
        );

        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public void onMainActionSelected() {
                Log.v("Alder", "Clicked Save");
                if (TextUtils.isEmpty(titleEditText.getText())) {
                    Log.v("Alder", "Title is empty");
                    changeNoteIntent.titleEmpty();
                } else if (current == null) {
                    String noteTitle = titleEditText.getText().toString();
                    String noteContent = noteContentEditText.getText().toString();

                    Note toSend = new Note(noteTitle, noteContent, getCurrentDateTime());

                    changeNoteIntent.addNote(toSend);
                } else {
                    current.title = titleEditText.getText().toString();
                    current.content = noteContentEditText.getText().toString();
                    changeNoteIntent.saveNote(current);
                }
//
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
            }
        });

        speedDialView.setOnActionSelectedListener(speedDialActionItem -> {
            switch (speedDialActionItem.getId()) {
                case R.id.fab_add:
                    showSnackBar("More coming soon!", R.color.colorAccent);
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
            changeNoteIntent = (ChangeNoteIntent) context;
        } catch (ClassCastException e) {
            Log.e("Alder", context.toString() + " Must implement ChangeNoteIntent");
            throw e;
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFromat = new SimpleDateFormat("MM/dd/yyyy  hh:mm  aa");
        return dateFromat.format(new Date());
    }

    private void showSnackBar(String test, int color) {
        Snackbar snackbar = Snackbar.make(rootView.findViewById(R.id.add_note_layout), test, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(color);
        snackbar.show();
    }

}
