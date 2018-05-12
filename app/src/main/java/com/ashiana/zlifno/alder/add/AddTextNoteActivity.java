package com.ashiana.zlifno.alder.add;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.Note;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.victorminerva.widget.edittext.AutofitEdittext;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;
import java.util.Objects;

public class AddTextNoteActivity extends Fragment {

    private AddTextNoteIntent intent;

    public interface AddTextNoteIntent {
        void addNote(Note note);

        void saveNote(Note note);

        void titleEmpty();
    }

    View rootView;
    private TextView titleEditText;
    private AutofitEdittext hiddenTitleEditText;
    private EditText noteContentEditText;
    private SpeedDialView speedDialView;
    private TextView noteTimeTextView;
    private Note current;

    public static boolean viaBack;

    public static final String EXTRA_CURRENT_NOTE = "com.ashiana.zlifno.alder.CURRENT_NOTE";
    public static final String SAVE_NOTE_EXTRA = "com.ashiana.zlifno.alder.SAVE_NOTE";
    public static final String UPDATE_NOTE_EXTRA = "com.ashiana.zlifno.alder.UPDATE_NOTE";

    private String usFormat = DateTimeFormat.patternForStyle("L-", Locale.US);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_text, container, false);

        initSpeedDial();

        changeBarColors(R.color.colorPrimaryDark);

        viaBack = false;


//        if (intent.hasExtra(EXTRA_CURRENT_NOTE)) {
//            current = (Note) intent.getSerializableExtra(EXTRA_CURRENT_NOTE);
//        }

        titleEditText = (TextView) rootView.findViewById(R.id.note_title);
        hiddenTitleEditText = (AutofitEdittext) rootView.findViewById(R.id.note_title_hidden);
        hiddenTitleEditText.setSingleLine(false);
        noteTimeTextView = (TextView) rootView.findViewById(R.id.note_time);
        noteContentEditText = (EditText) rootView.findViewById(R.id.note_content);

        if (current != null) {
            titleEditText.setText(current.title);
            hiddenTitleEditText.setText(titleEditText.getText());
            noteContentEditText.setText(current.content);
            noteTimeTextView.setText(current.timeCreated);
        } else {
            DateTime dateTime = DateTime.now();
            noteTimeTextView.setText(dateTime.toString(usFormat));
        }

        titleEditText.setOnClickListener(v -> {
            titleEditText.setVisibility(View.INVISIBLE);
            if (current != null) {
                hiddenTitleEditText.setText(titleEditText.getText());
            }
            hiddenTitleEditText.setVisibility(View.VISIBLE);
            hiddenTitleEditText.requestFocus();
            hiddenTitleEditText.setSelection(hiddenTitleEditText.getText().length());
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        });

        return rootView;
    }

    private void changeBarColors(int color) {
        getActivity().getWindow().setStatusBarColor(getResources().getColor(color));
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(color));
    }

    private void initSpeedDial() {

        speedDialView = (SpeedDialView) rootView.findViewById(R.id.speedDialAddMenu);

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
                    intent.titleEmpty();
                } else if (current == null) {
                    String noteTitle = hiddenTitleEditText.getText().toString();
                    String noteContent = noteContentEditText.getText().toString();

                    DateTime dateTime = DateTime.now();
                    String usFormat = DateTimeFormat.patternForStyle("L-", Locale.US);

                    Note toSend = new Note(noteTitle, Note.NOTE_TYPE_TEXT, noteContent, dateTime.toString(usFormat));

                    intent.addNote(toSend);

                } else {
                    current.title = hiddenTitleEditText.getText().toString();
                    current.content = noteContentEditText.getText().toString();
                    saveNoteIntent.putExtra(UPDATE_NOTE_EXTRA, current);
                    intent.saveNote(current);
                }
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
        Snackbar snackbar = Snackbar.make(rootView.findViewById(R.id.add_note_layout), test, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(color);
        snackbar.show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
        if (hiddenTitleEditText.hasFocus()) {
            hiddenTitleEditText.setInputType(InputType.TYPE_NULL);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hideKeyboard();
        }
        return super.onOptionsItemSelected(item);
    }
}


