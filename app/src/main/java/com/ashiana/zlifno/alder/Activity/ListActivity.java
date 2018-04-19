package com.ashiana.zlifno.alder.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ashiana.zlifno.alder.Fragment.AddTextNoteFragment;
import com.ashiana.zlifno.alder.Fragment.ListFragment;
import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.TextNote;

public class ListActivity extends AppCompatActivity implements ListFragment.MainIntents, AddTextNoteFragment.ChangeNoteIntent {

    private ListFragment listFragment;
    private FragmentManager fragmentManager;
    private SharedPreferences prefs;
//    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // For testing with SharedPreferences
        prefs = getSharedPreferences("alder_prefs", Context.MODE_PRIVATE);
//        editor = prefs.edit();
//        editor.clear();
//        editor.apply();

        fragmentManager = getSupportFragmentManager();
//        listFragment = (ListFragment) fragmentManager.findFragmentById(R.id.root_activity);
        listFragment = ListFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root_activity, listFragment)
                .commit();
    }

    public void showFragmentWithTransition(Fragment current, Fragment newFragment, String tag, View sharedView, String sharedElementName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // check if the fragment is in back stack
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(tag, 0);
        if (fragmentPopped) {
            // fragment is popped from backStack
        } else {
            current.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
            current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));

            newFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
            newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));

            ViewCompat.setTransitionName(sharedView, sharedElementName);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.root_activity, newFragment);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.addSharedElement(sharedView, sharedElementName);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (prefs.getBoolean(ListFragment.TAG_FINISHED_FINAL_SPOTLIGHT, false)) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                String last = getSupportFragmentManager().getBackStackEntryAt(
                        getSupportFragmentManager()
                                .getBackStackEntryCount() - 1)
                        .getName();

                // Coming back from AddTextNote
                if (last.equals("AddTextNote")) {
                    listFragment.closeFAB();
                    super.onBackPressed();
//                changeBarColors(R.color.colorPrimary);
                }
            } else {
                if (!listFragment.closeFAB()) {
                    super.onBackPressed();
                }
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
    public void updateNote(TextNote textNote, int position, View v) {
//        changeBarColors(R.color.colorAccent);

        AddTextNoteFragment fragment = new AddTextNoteFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", "transition" + position);
        args.putSerializable("current", textNote);
        fragment.setArguments(args);

        showFragmentWithTransition(listFragment, fragment, "AddTextNote", v, "transition" + position);
//        if (getSupportFragmentManager().findFragmentByTag("AddTextNote") == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.root_activity, fragment)
//                    .addToBackStack("AddTextNote")
//                    .commit();
//        } else {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.root_activity, fragment, "AddTextNote");
//        }
    }


//            ((MainActivity) context).showFragmentWithTransition(this, movieDetail, "movieDetail", view, "transition" + position);

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
        showSnackBar("Title can't be empty", android.R.color.holo_red_light);
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 1 seconds
        assert v != null;
        v.vibrate(1000);
    }

    private void showSnackBar(String test, int color) {
//        CafeBar.make(findViewById(R.id.coordinator_layout), test, CafeBar.Duration.MEDIUM).show();

        Snackbar snackbar;
        snackbar = Snackbar.make(this.findViewById(R.id.coordinator_layout), test, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(color));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();

    }
}