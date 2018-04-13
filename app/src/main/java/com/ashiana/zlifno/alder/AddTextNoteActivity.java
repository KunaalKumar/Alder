package com.ashiana.zlifno.alder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ashiana.zlifno.to_do.R;
import com.ashiana.zlifno.alder.data.Note;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.victorminerva.widget.edittext.AutofitEdittext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import jahirfiquitiva.libs.fabsmenu.TitleFAB;
import maes.tech.intentanim.CustomIntent;

public class AddTextNoteActivity extends AppCompatActivity {

    public static final String SAVE_NOTE_EXTRA = "com.ashiana.zlifno.to_do.SAVE_NOTE";

    private AutofitEdittext titleEditText;
    private MaterialEditText noteContentEditText;
    private TitleFAB saveNoteFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_view_layout);

        Objects.requireNonNull(getSupportActionBar()).setElevation(4);

        titleEditText = findViewById(R.id.note_title);

        noteContentEditText = findViewById(R.id.note_content);
        noteContentEditText.setMetTextColor(Color.WHITE);

        saveNoteFab = findViewById(R.id.save_note);

        saveNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("APPD", "Clicked Save");
                Intent saveNoteIntent = new Intent();
                if (TextUtils.isEmpty(titleEditText.getText())) {
                    Log.v("APPD", "Title is empty");
                    setResult(RESULT_CANCELED, saveNoteIntent);
                } else {
                    String noteTitle = titleEditText.getText().toString();
                    String noteContent = noteContentEditText.getText().toString();

                    SimpleDateFormat dateFromat = new SimpleDateFormat("MM/dd/yyyy  hh:mm  aa");

                    Note toSend = new Note(noteTitle, noteContent,"Time created: " +  dateFromat.format(new Date()));

                    saveNoteIntent.putExtra(SAVE_NOTE_EXTRA, toSend);
                    setResult(RESULT_OK, saveNoteIntent);
                    StyleableToast.makeText(getApplicationContext(),
                            "Note Made!",
                            Toast.LENGTH_LONG,
                            R.style.note_made_toast)
                            .show();
                }
                finish();
                CustomIntent.customType(AddTextNoteActivity.this, "up-to-bottom");
            }
        });

        CustomIntent.customType(AddTextNoteActivity.this, "bottom-to-up");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        CustomIntent.customType(AddTextNoteActivity.this, "up-to-bottom");
    }
}
