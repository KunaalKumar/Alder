package com.ashiana.zlifno.alder.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

import maes.tech.intentanim.CustomIntent;

public class AddTextNoteActivity extends AppCompatActivity {

    public static final String SAVE_NOTE_EXTRA = "com.ashiana.zlifno.to_do.SAVE_NOTE";

    private AutofitEdittext titleEditText;
    private MaterialEditText noteContentEditText;
    private SpeedDialView speedDialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_view_layout);

        titleEditText = findViewById(R.id.note_title);

        noteContentEditText = findViewById(R.id.note_content);
        noteContentEditText.setMetTextColor(Color.WHITE);

        initSpeedDial();

        CustomIntent.customType(AddTextNoteActivity.this, "up-to-bottom");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        CustomIntent.customType(AddTextNoteActivity.this, "bottom-to-up");
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
            public void onMainActionSelected() {
                Log.v("Alder", "Clicked Save");
                Intent saveNoteIntent = new Intent();
                if (TextUtils.isEmpty(titleEditText.getText())) {
                    Log.v("APPD", "Title is empty");
                    setResult(RESULT_CANCELED, saveNoteIntent);
                } else {
                    String noteTitle = titleEditText.getText().toString();
                    String noteContent = noteContentEditText.getText().toString();

                    SimpleDateFormat dateFromat = new SimpleDateFormat("MM/dd/yyyy  hh:mm  aa");

                    Note toSend = new Note(noteTitle, noteContent, "Time created: " + dateFromat.format(new Date()));

                    saveNoteIntent.putExtra(SAVE_NOTE_EXTRA, toSend);
                    setResult(RESULT_OK, saveNoteIntent);
                    StyleableToast.makeText(getApplicationContext(),
                            "Note Made!",
                            Toast.LENGTH_LONG,
                            R.style.note_made_toast)
                            .show();
                }
                finish();
                CustomIntent.customType(AddTextNoteActivity.this, "bottom-to-up");
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

    private void showSnackBar(String test) {
        android.support.design.widget.Snackbar
                .make(findViewById(R.id.add_note_layout), test, android.support.design.widget.Snackbar.LENGTH_LONG).show();
    }

}
