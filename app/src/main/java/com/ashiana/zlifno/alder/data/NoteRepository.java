package com.ashiana.zlifno.alder.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

// Makes an API off of the Dao
public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> notesList;

    public NoteRepository(Application application) {
        NoteRoomDatabase database = NoteRoomDatabase.getDatabase(application);
        noteDao = database.noteDao();
        notesList = noteDao.getAllNotes();
    }

    public LiveData<List<Note>> getNotesList() {
        return notesList;
    }


    public void insertNote(Note note) {
        note.setPosition(notesList.getValue().size() + 1);
        new insertAsyncTask(noteDao).execute(note);
    }

    private static class insertAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao asyncTaskNoteDao;

        insertAsyncTask(NoteDao dao) {
            asyncTaskNoteDao = dao;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            Log.v("APPD", "REPO: Started adding note in background");
            asyncTaskNoteDao.insertNote(params[0]);
            return null;
        }
    }

    public void deleteNote(Note note) {

        new deleteAsyncTask(noteDao).execute(note);
    }

    private static class deleteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao asyncTaskNoteDao;

        deleteAsyncTask(NoteDao dao) {
            asyncTaskNoteDao = dao;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            asyncTaskNoteDao.deleteNote(params[0]);
            return null;
        }
    }

    public void updateNote(Note note) {
        new updateAsyncTask(noteDao).execute(note);
    }

    private static class updateAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao asyncTaskNoteDao;

        updateAsyncTask(NoteDao dao) {
            asyncTaskNoteDao = dao;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            asyncTaskNoteDao.updateNote(params[0]);
            return null;
        }
    }

    // Note moved up
    public void moveNoteUp(List<Note> notes) {
        new moveNoteUpAsyncTask(noteDao, notes).execute();
    }

    private static class moveNoteUpAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        private List<Note> notes;

        moveNoteUpAsyncTask(NoteDao noteDao, List<Note> notes) {
            this.noteDao = noteDao;
            this.notes = notes;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Note tempNote = notes.get(notes.size() - 1);
            noteDao.deleteNoteById(tempNote.getId());
            Note nextNote;
            int notePosition = notes.get(0).getPosition();

            for (int i = 0; i < notes.size(); i++) {

                nextNote = notes.get(i);
                noteDao.deleteNoteByPosition(notePosition);

                tempNote.setPosition(notePosition);
                noteDao.insertNote(tempNote);

                tempNote = nextNote;
                notePosition++;

            }
            return null;
        }
    }

    // Note moved down
    public void moveNoteDown(List<Note> notes) {
        new moveNoteDownAsyncTask(noteDao, notes).execute();
    }

    private static class moveNoteDownAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        private List<Note> notes;

        moveNoteDownAsyncTask(NoteDao noteDao, List<Note> notes) {
            this.noteDao = noteDao;
            this.notes = notes;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Note tempNote = notes.get(0);
            noteDao.deleteNoteById(tempNote.getId());
            Note nextNote;
            int notePosition = notes.get(notes.size() - 1).getPosition();

            for (int i = notes.size() - 1; i >= 0; i--) {

                nextNote = notes.get(i);
                noteDao.deleteNoteByPosition(notePosition);

                tempNote.setPosition(notePosition);
                noteDao.insertNote(tempNote);

                tempNote = nextNote;
                notePosition--;

            }
            return null;
        }
    }
}
