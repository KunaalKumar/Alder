package com.ashiana.zlifno.alder.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.ashiana.zlifno.alder.data.Note;
import com.ashiana.zlifno.alder.data.NoteRepository;

import java.util.List;

// Passes on data to UI
public class ListViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private LiveData<List<Note>> notesList;

    public ListViewModel(Application application) {
        super(application);
        repository = new NoteRepository(application);
        notesList = repository.getNotesList();
    }

    public LiveData<List<Note>> getNotesList() {
        return notesList;
    }

    public void insertNote(Note note) {

        Log.v("APPD", "ListViewModel : Sending \"" + note.getTitle() + "\" to repository");

        repository.insertNote(note);
    }

//    public void deleteNote(Note note) {
//        repository.deleteNote(note);
//    }
//
//    public void updateNote(Note note) {
//        repository.updateNote(note);
//    }

}


