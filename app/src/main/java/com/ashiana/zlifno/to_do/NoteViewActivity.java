package com.ashiana.zlifno.to_do;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.victorminerva.widget.edittext.AutofitEdittext;

import jahirfiquitiva.libs.fabsmenu.TitleFAB;
import maes.tech.intentanim.CustomIntent;

public class NoteViewActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE = "com.ashiana.zlifno.to_do.NOTE";

    private AutofitEdittext titleEditText;
    private MaterialEditText noteEditText;
    private TitleFAB saveNoteFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_view_layout);

        getSupportActionBar().setElevation(4);

        titleEditText = findViewById(R.id.note_title);

        noteEditText = findViewById(R.id.note_content);
        noteEditText.setMetTextColor(Color.WHITE);

        saveNoteFab = findViewById(R.id.save_note);

        saveNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("APPD", "Clicked Save");
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(titleEditText.getText())) {
                    Log.v("APPD", "Title is empty");
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String noteTitle = titleEditText.getText().toString();
                    Log.v("APPD", "Title isn't empty!");
                    replyIntent.putExtra(EXTRA_NOTE, noteTitle);
                    setResult(RESULT_OK, replyIntent);
                    StyleableToast.makeText(getApplicationContext(),
                            "Note Made!",
                            Toast.LENGTH_LONG,
                            R.style.note_made_toast)
                            .show();
                }
                finish();
                CustomIntent.customType(NoteViewActivity.this, "up-to-bottom");
            }
        });

        CustomIntent.customType(NoteViewActivity.this, "bottom-to-up");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CustomIntent.customType(NoteViewActivity.this, "up-to-bottom");
    }
}
