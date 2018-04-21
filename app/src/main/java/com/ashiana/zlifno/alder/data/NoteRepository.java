package com.ashiana.zlifno.alder.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.ashiana.zlifno.alder.view_model.ListViewModel;

import java.util.List;

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

    public void updateNote(Note note) {
        new updateAsyncTask(noteDao).execute(note);
    }

    private static class updateAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao asynTaskNoteDao;

        updateAsyncTask(NoteDao dao) {
            asynTaskNoteDao = dao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            Log.v("Alder", "REPO: Started updating note in background");
            asynTaskNoteDao.updateNote(notes[0]);
            return null;
        }
    }

    public void insertNote(Note note) {
        note.position = notesList.getValue().size() + 1;
        new insertAsyncTask(noteDao).execute(note);
    }

    private static class insertAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao asyncTaskNoteDao;

        insertAsyncTask(NoteDao dao) {
            asyncTaskNoteDao = dao;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            Log.v("Alder", "REPO: Started adding note in background");
            asyncTaskNoteDao.insertNote(params[0]);
            return null;
        }
    }

    public void deleteNote(Note note) {

        new deleteAsyncTask(noteDao, notesList.getValue()).execute(note);

    }

    private static class deleteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao asyncTaskNoteDao;
        private List<Note> notes;

        deleteAsyncTask(NoteDao dao, List<Note> notes) {
            asyncTaskNoteDao = dao;
            this.notes = notes;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            asyncTaskNoteDao.deleteNote(params[0]);
            Note next = asyncTaskNoteDao.getNoteByPos(params[0].position + 1);
            if (next != null) {
                moveNotePosUp(next.position);
            }
            return null;
        }

        private void moveNotePosUp(int firstItemPos) {
            Note current;
            for (int i = firstItemPos; i <= notes.size(); i++) {
                current = asyncTaskNoteDao.getNoteByPos(i);
                current.position = i - 1;
                asyncTaskNoteDao.updateNote(current);
            }
        }
    }

    // Note moved up
    public void moveNote(Note holdingNote, Note destNote, ListViewModel model) {
        model.inProgress = true;
        synchronized (this) {
            new moveNoteAsyncTask(noteDao, holdingNote, destNote, model).execute();
        }
    }

    private static class moveNoteAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        private Note holdingNote;
        private Note destNote;
        private ListViewModel model;

        moveNoteAsyncTask(NoteDao noteDao, Note holdingNote, Note destNote, ListViewModel model) {
            this.noteDao = noteDao;
            this.holdingNote = holdingNote;
            this.destNote = destNote;
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            int fromPosition = holdingNote.position;
            int toPosition = destNote.position;

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    swap(i, i + 1);
                }

                // Moved up
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    swap(i, i - 1);
                }
            }
            model.inProgress = false;
            return null;
        }

        private void swap(int firstPos, int secondPos) {
            Note firstNote = noteDao.getNoteByPos(firstPos);
            Note secondNote = noteDao.getNoteByPos(secondPos);

            firstNote.position = secondPos;
            secondNote.position = firstPos;

            noteDao.updateNote(firstNote);
            noteDao.updateNote(secondNote);
        }
    }
}
