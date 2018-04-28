package com.ashiana.zlifno.alder.Activity.add;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;
import java.util.Objects;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class AddTextNoteActivity extends SwipeBackActivity {

    private TextView titleEditText;
    private AutofitEdittext hiddenTitleEditText;
    private EditText noteContentEditText;
    private SpeedDialView speedDialView;
    private TextView noteTimeTextView;
    private Note current;
    public static boolean viaBack;
    public static boolean viaSwipe;

    public static final String EXTRA_CURRENT_NOTE = "com.ashiana.zlifno.alder.CURRENT_NOTE";
    public static final String SAVE_NOTE_EXTRA = "com.ashiana.zlifno.alder.SAVE_NOTE";
    public static final String UPDATE_NOTE_EXTRA = "com.ashiana.zlifno.alder.UPDATE_NOTE";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SimpleTarget titleSpotlight, noteContentSpotlight, noteTimeSpotlight, saveFabSpotlight, titleSpotlight2;
    public static String TAG_FINISHED_ADD_NOTE_SPOTLIGHT = "FINISHED_ADD_NOTE_SPOTLIGHT";

    private String usFormat = DateTimeFormat.patternForStyle("L-", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_text);

        initSpeedDial();

        changeBarColors(R.color.colorPrimaryDark);

        viaBack = false;
        viaSwipe = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_CURRENT_NOTE)) {
            current = (Note) intent.getSerializableExtra(EXTRA_CURRENT_NOTE);
        }

        titleEditText = (TextView) findViewById(R.id.note_title);
        hiddenTitleEditText = (AutofitEdittext) findViewById(R.id.note_title_hidden);
        hiddenTitleEditText.setSingleLine(false);
        noteTimeTextView = (TextView) findViewById(R.id.note_time);
        noteContentEditText = (EditText) findViewById(R.id.note_content);

        if (current != null) {
            titleEditText.setText(current.title);
            hiddenTitleEditText.setText(titleEditText.getText());
            noteContentEditText.setText(current.content);
            noteTimeTextView.setText(current.timeCreated);
        } else {
            DateTime dateTime = DateTime.now();
            noteTimeTextView.setText(dateTime.toString(usFormat));
        }

        View rootView = findViewById(R.id.add_note_layout);

        titleEditText.setOnClickListener(v -> {
            titleEditText.setVisibility(View.INVISIBLE);
            if (current != null) {
                hiddenTitleEditText.setText(titleEditText.getText());
            }
            hiddenTitleEditText.setVisibility(View.VISIBLE);
            hiddenTitleEditText.requestFocus();
            hiddenTitleEditText.setSelection(hiddenTitleEditText.getText().length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        });

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                sharedPreferences = getSharedPreferences("alder_prefs", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                initSpotlights();
                if (!sharedPreferences.getBoolean(TAG_FINISHED_ADD_NOTE_SPOTLIGHT, false)) {

                    // callback when Spotlight ends
                    Spotlight.with(AddTextNoteActivity.this)
                            .setOverlayColor(ContextCompat.getColor(AddTextNoteActivity.this, R.color.background)) // background overlay color
                            .setDuration(1000L) // duration of Spotlight emerging and disappearing in ms
                            .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                            .setTargets(titleSpotlight, noteContentSpotlight, noteTimeSpotlight, saveFabSpotlight, titleSpotlight2)
                            .setClosedOnTouchedOutside(true) // set if target is closed when touched outside
                            .setOnSpotlightEndedListener(() -> {
                                if (sharedPreferences.getBoolean(TAG_FINISHED_ADD_NOTE_SPOTLIGHT, true)) {
                                    hiddenTitleEditText.setFocusableInTouchMode(true);
                                    hiddenTitleEditText.requestFocus();
                                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                            .showSoftInput(hiddenTitleEditText, InputMethodManager.SHOW_FORCED);
                                }
                            })
                            .start(); // start Spotlight

                    editor.putBoolean(TAG_FINISHED_ADD_NOTE_SPOTLIGHT, true);
                    editor.apply();
                }
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void changeBarColors(int color) {
        getWindow().setStatusBarColor(getResources().getColor(color));
        getWindow().setNavigationBarColor(getResources().getColor(color));
    }

    public static AddTextNoteActivity newInstance() {
        return new AddTextNoteActivity();
    }

    private void initSpotlights() {
        titleSpotlight = new SimpleTarget.Builder(AddTextNoteActivity.this)
                .setPoint(titleEditText) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(400f) // radius of the Target
                .setTitle("Note Title") // title
                .setDescription("Enter a title for your first note") // description
                .build();
        noteContentSpotlight = new SimpleTarget.Builder(AddTextNoteActivity.this)
                .setPoint(noteContentEditText) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(1000f) // radius of the Target
                .setTitle("Note Content") // title
                .setDescription("This is where you can add the content for your note") // description
                .build();
        noteTimeSpotlight = new SimpleTarget.Builder(AddTextNoteActivity.this)
                .setPoint(noteTimeTextView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(500f) // radius of the Target
                .setTitle("Time Created") // title
                .setDescription("Here you can see the time your note was created") // description
                .build();
        saveFabSpotlight = new SimpleTarget.Builder(AddTextNoteActivity.this)
                .setPoint(speedDialView) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(200f) // radius of the Target
                .setTitle("Save Button") // title
                .setDescription("Click here to save your note") // description
                .build();
        titleSpotlight2 = new SimpleTarget.Builder(AddTextNoteActivity.this)
                .setPoint(titleEditText) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(400) // radius of the Target
                .setTitle("Alright") // title
                .setDescription("Let's make your first note, enter the title and click the save button") // description
                .build();
    }

    private void initSpeedDial() {

        speedDialView = (SpeedDialView) findViewById(R.id.speedDialAddMenu);

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
                hideKeyboard();
                Log.v("Alder", "Clicked Save");
                Intent saveNoteIntent = new Intent();
                if (TextUtils.isEmpty(hiddenTitleEditText.getText())) {
                    Log.v("Alder", "Title is empty");
                    viaSwipe = false;
                    setResult(RESULT_CANCELED, saveNoteIntent);
                } else if (current == null) {
                    String noteTitle = hiddenTitleEditText.getText().toString();
                    String noteContent = noteContentEditText.getText().toString();

                    DateTime dateTime = DateTime.now();
                    String usFormat = DateTimeFormat.patternForStyle("L-", Locale.US);

                    Note toSend = new Note(noteTitle, Note.NOTE_TYPE_TEXT, noteContent, dateTime.toString(usFormat));

                    saveNoteIntent.putExtra(SAVE_NOTE_EXTRA, toSend);
                    setResult(RESULT_OK, saveNoteIntent);
                } else {
                    current.title = hiddenTitleEditText.getText().toString();
                    current.content = noteContentEditText.getText().toString();
                    saveNoteIntent.putExtra(UPDATE_NOTE_EXTRA, current);
                    setResult(RESULT_OK, saveNoteIntent);
                }

                finish();
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

    private void showSnackBar(String test, int color) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.add_note_layout), test, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(color);
        snackbar.show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
    }

}


