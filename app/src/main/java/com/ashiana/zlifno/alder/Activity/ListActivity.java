package com.ashiana.zlifno.alder.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ashiana.zlifno.alder.Fragment.AddTextNoteFragment;
import com.ashiana.zlifno.alder.Fragment.ListFragment;
import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.TextNote;

public class ListActivity extends AppCompatActivity implements ListFragment.MainIntents, AddTextNoteFragment.ChangeNoteIntent {

    private ListFragment listFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.root_activity);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            String last = getSupportFragmentManager().getBackStackEntryAt(
                    getSupportFragmentManager()
                            .getBackStackEntryCount() - 1)
                    .getName();

            // Coming back from AddTextNote
            if (last.equals("AddTextNote")) {
                listFragment.closeFAB();
//                changeBarColors(R.color.colorPrimary);
                super.onBackPressed();
            }
        } else {
            if (!listFragment.closeFAB()) {
                super.onBackPressed();
            }
        }
    }

    private void changeBarColors(int color) {
        getWindow().setStatusBarColor(getResources().getColor(color));
        getWindow().setNavigationBarColor(getResources().getColor(color));
        findViewById(R.id.my_toolbar).setBackgroundColor(getResources().getColor(color));
    }

    @Override
    public void newNote() {

//        changeBarColors(R.color.colorAccent);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.root_activity, new AddTextNoteFragment())
                .addToBackStack("AddTextNote")
                .commit();
    }

    // Called on touch
    @Override
    public void updateNote(TextNote textNote) {
//        changeBarColors(R.color.colorAccent);

        Bundle args = new Bundle();
        args.putSerializable("current", textNote);

        AddTextNoteFragment fragment = new AddTextNoteFragment();
        fragment.putArguments(args);

        if (getSupportFragmentManager().findFragmentByTag("AddTextNote") == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root_activity, fragment)
                    .addToBackStack("AddTextNote")
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_activity, fragment, "AddTextNote");
        }

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.root_activity).getWindowToken(), 0);
    }

    // New textNote to add
    @Override
    public void addNote(TextNote textNote) {
//        changeBarColors(R.color.colorPrimary);
        listFragment.closeFAB();
        hideKeyboard();
        getSupportFragmentManager().popBackStack();
        listFragment.addNote(textNote);
    }

    // Update contents of given textNote
    @Override
    public void saveNote(TextNote textNote) {
//        changeBarColors(R.color.colorPrimary);
        listFragment.closeFAB();
        hideKeyboard();
        getSupportFragmentManager().popBackStack();
        listFragment.saveNote(textNote);
    }

    @Override
    public void titleEmpty() {
//        changeBarColors(R.color.colorPrimary);
        listFragment.closeFAB();
        hideKeyboard();
        getSupportFragmentManager().popBackStack();
        Toast.makeText(this, "Title is empty", Toast.LENGTH_LONG).show();
    }
}