package com.ashiana.zlifno.alder.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.Note;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.victorminerva.widget.edittext.AutofitEdittext;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AddTextNoteActivity extends AppCompatActivity {

    public static final String SAVE_NOTE_EXTRA = "com.ashiana.zlifno.alder.SAVE_NOTE";
    public static final String UPDATE_NOTE_EXTRA = "com.ashiana.zlifno.alder.UPDATE_NOTE";
    public static final String EXTRA_CIRCULAR_REVEAL_X = "com.ashiana.zlifno.alderEXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "com.ashiana.zlifno.alderEXTRA_CIRCULAR_REVEAL_Y";
    public static final String EXTRA_CURRENT_NOTE = "com.ashiana.zlifno.alder.CURRENT_NOTE";

    View rootLayout;
    private int revealX;
    private int revealY;

    private AutofitEdittext titleEditText;
    private EditText noteContentEditText;
    private SpeedDialView speedDialView;
    private Note current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_view_layout);

        final Intent intent = getIntent();

        rootLayout = findViewById(R.id.add_note_layout);

        if (savedInstanceState == null &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {

            if (intent.hasExtra(EXTRA_CURRENT_NOTE)) {
                current = (Note) intent.getSerializableExtra(EXTRA_CURRENT_NOTE);
            }

            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);


            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }

        titleEditText = findViewById(R.id.note_title);

        noteContentEditText = findViewById(R.id.note_content);
        noteContentEditText.setTextColor(Color.WHITE);

        if (current != null) {
            titleEditText.setText(current.getTitle());
            noteContentEditText.setText(current.getContent());
        }

        initSpeedDial();

    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(400);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    protected void unRevealActivity() {
        float startRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                rootLayout, revealX, revealY, startRadius, 0);

        circularReveal.setDuration(400);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rootLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });
        circularReveal.start();
    }

    @Override
    public void onBackPressed() {

        // TODO:  Add check to see if content changed, to save dialog

        unRevealActivity();
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void initSpeedDial() {

        speedDialView = findViewById(R.id.speedDialAddMenu);

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.save_note, R.drawable.ic_arrow_drop_down_white_24dp)
                        .setLabel("More coming soon!")
                        .setFabBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setLabelBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setLabelColor(Color.WHITE)
                        .create()
        );

        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                Log.v("Alder", "Clicked Save");
                Intent saveNoteIntent = new Intent();
                if (TextUtils.isEmpty(titleEditText.getText())) {
                    Log.v("APPD", "Title is empty");
                    setResult(RESULT_CANCELED, saveNoteIntent);
                } else {
                    if (current != null) {
                        current.setTitle(titleEditText.getText().toString());
                        current.setContent(noteContentEditText.getText().toString());
                        saveNoteIntent.putExtra(UPDATE_NOTE_EXTRA, current);
                        setResult(RESULT_OK, saveNoteIntent);
                    } else {
                        String noteTitle = titleEditText.getText().toString();
                        String noteContent = noteContentEditText.getText().toString();

                        SimpleDateFormat dateFromat = new SimpleDateFormat("MM/dd/yyyy  hh:mm  aa");

                        Note toSend = new Note(noteTitle, noteContent, "Time created: " + dateFromat.format(new Date()));

                        saveNoteIntent.putExtra(SAVE_NOTE_EXTRA, toSend);
                        setResult(RESULT_OK, saveNoteIntent);
                    }

                }
                unRevealActivity();
                finish();
                return false;
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

}
