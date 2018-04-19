package com.ashiana.zlifno.alder.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.TextNote;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.victorminerva.widget.edittext.AutofitEdittext;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddTextNoteFragment extends Fragment {

    private ChangeNoteIntent changeNoteIntent;

    public interface ChangeNoteIntent {
        void addNote(TextNote textNote);

        void saveNote(TextNote textNote);

        void titleEmpty();
    }

    View rootView;

    private AutofitEdittext titleEditText;
    private EditText noteContentEditText;
    private SpeedDialView speedDialView;
    private TextNote current;
    public static boolean viaBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_note, container, false);
        initSpeedDial();

        viaBack = false;

        titleEditText = rootView.findViewById(R.id.note_title);
        TextView noteTimeTextView = rootView.findViewById(R.id.note_time);
        noteContentEditText = rootView.findViewById(R.id.note_content);

        if (current != null) {
            titleEditText.setText(current.getTitle());
            noteContentEditText.setText(current.getContent());
            noteTimeTextView.setText(current.getTimeCreated());
        } else {
            noteTimeTextView.setText(getCurrentDateTime());
        }

        return rootView;
    }

    private void initSpeedDial() {

        speedDialView = rootView.findViewById(R.id.speedDialAddMenu);

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
                if (TextUtils.isEmpty(titleEditText.getText())) {
                    Log.v("Alder", "Title is empty");
                    changeNoteIntent.titleEmpty();
                } else if (current == null) {
                    String noteTitle = titleEditText.getText().toString();
                    String noteContent = noteContentEditText.getText().toString();

                    TextNote toSend = new TextNote(noteTitle, noteContent, getCurrentDateTime());

                    changeNoteIntent.addNote(toSend);
                } else {
                    current.setTitle(titleEditText.getText().toString());
                    current.setContent(noteContentEditText.getText().toString());
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

    public void putArguments(Bundle args) {
        current = (TextNote) args.getSerializable("current");
    }
}
