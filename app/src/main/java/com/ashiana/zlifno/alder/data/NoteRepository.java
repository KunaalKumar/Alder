package com.ashiana.zlifno.alder.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ashiana.zlifno.alder.view_model.ListViewModel;

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
    public void moveNote(Note holdingNote, Note destNote, ListViewModel model) {
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
            model.inProgress = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            int fromPosition = holdingNote.getPosition();
            int toPosition = destNote.getPosition();

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

            firstNote.setPosition(secondPos);
            secondNote.setPosition(firstPos);

            noteDao.updateNote(firstNote);
            noteDao.updateNote(secondNote);
        }
    }
}
