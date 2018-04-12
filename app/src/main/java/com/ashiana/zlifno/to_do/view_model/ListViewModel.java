package com.ashiana.zlifno.to_do.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.ashiana.zlifno.to_do.data.Note;
import com.ashiana.zlifno.to_do.data.NoteRepository;

import java.util.List;

// Passes on data to UI
public class ListViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private LiveData<List<Note>> notesList;

    public ListViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        notesList = repository.getNotesList();
    }

    public LiveData<List<Note>> getNotesList() {
        return notesList;
    }

    public LiveData<List<Note>> getNotesListByTimeAsc() {
        notesList = repository.getNotesListByTimeAsc();
        return notesList;
    }

    public LiveData<List<Note>> getNotesListByTimeDesc() {
        notesList = repository.getNotesListByTimeDesc();
        return notesList;
    }

    public void insertNote(Note note) {
        repository.insertNote(note);
    }

    public void deleteNote(Note note) {
        repository.deleteNote(note);
    }

    public void updateNote(Note note) {
        repository.updateNote(note);
    }

}
