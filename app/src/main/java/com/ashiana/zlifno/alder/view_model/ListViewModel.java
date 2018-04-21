package com.ashiana.zlifno.alder.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ashiana.zlifno.alder.data.Note;
import com.ashiana.zlifno.alder.data.NoteRepository;

import java.util.List;

// Passes on data to UI
public class ListViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private LiveData<List<Note>> notesList;
    public boolean inProgress;

    public ListViewModel(Application application) {
        super(application);
        repository = new NoteRepository(application);
        notesList = repository.getNotesList();
        inProgress = false;
    }

    public LiveData<List<Note>> getNotesList() {
        return notesList;
    }

    public void insertNote(Note note) {

        Log.v("Alder", "ListViewModel : Sending \"" + note.title + "\" to repository");

        repository.insertNote(note);
    }

    public void deleteNote(Note note) {
        Log.v("Alder", "ListViewModel : Deleting note id - " + note.title);

        repository.deleteNote(note);
    }

    public void moveNote(Note holdingNote, Note destinationNote, RecyclerView.Adapter adapter) {

        Log.v("Alder", "ListViewModel : Moving note to new position");

        repository.moveNote(holdingNote, destinationNote, this);
    }

    public void updateNote(Note note) {

        Log.v("Alder", "ListViewModel : Updating note");

        repository.updateNote(note);
    }


}


