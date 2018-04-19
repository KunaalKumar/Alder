package com.ashiana.zlifno.alder.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ashiana.zlifno.alder.data.TextNote;
import com.ashiana.zlifno.alder.data.NoteRepository;

import java.util.List;

// Passes on data to UI
public class ListViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private LiveData<List<TextNote>> notesList;
    public boolean inProgress;

    public ListViewModel(Application application) {
        super(application);
        repository = new NoteRepository(application);
        notesList = repository.getNotesList();
        inProgress = false;
    }

    public LiveData<List<TextNote>> getNotesList() {
        return notesList;
    }

    public void insertNote(TextNote textNote) {

        Log.v("Alder", "ListViewModel : Sending \"" + textNote.getTitle() + "\" to repository");

        repository.insertNote(textNote);
    }

    public void deleteNote(TextNote textNote) {
        Log.v("Alder", "ListViewModel : Deleting textNote id - " + textNote.getTitle());

        repository.deleteNote(textNote);
    }

    public void moveNote(TextNote holdingTextNote, TextNote destinationTextNote, RecyclerView.Adapter adapter) {

        Log.v("Alder", "ListViewModel : Moving note to new position");

        repository.moveNote(holdingTextNote, destinationTextNote, this);
    }

    public void updateNote(TextNote textNote) {

        Log.v("Alder", "ListViewModel : Updating textNote");

        repository.updateNote(textNote);
    }


}


