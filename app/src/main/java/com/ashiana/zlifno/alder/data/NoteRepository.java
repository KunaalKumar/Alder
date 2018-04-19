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
    private LiveData<List<TextNote>> notesList;

    public NoteRepository(Application application) {
        NoteRoomDatabase database = NoteRoomDatabase.getDatabase(application);
        noteDao = database.noteDao();
        notesList = noteDao.getAllNotes();
    }

    public LiveData<List<TextNote>> getNotesList() {
        return notesList;
    }

    public void updateNote(TextNote textNote) {
        new updateAsyncTask(noteDao).execute(textNote);
    }

    private static class updateAsyncTask extends AsyncTask<TextNote, Void, Void> {
        private NoteDao asynTaskNoteDao;

        updateAsyncTask(NoteDao dao) {
            asynTaskNoteDao = dao;
        }

        @Override
        protected Void doInBackground(TextNote... textNotes) {
            Log.v("Alder", "REPO: Started updating note in background");
            asynTaskNoteDao.updateNote(textNotes[0]);
            return null;
        }
    }

    public void insertNote(TextNote textNote) {
        textNote.setPosition(notesList.getValue().size() + 1);
        new insertAsyncTask(noteDao).execute(textNote);
    }

    private static class insertAsyncTask extends AsyncTask<TextNote, Void, Void> {

        private NoteDao asyncTaskNoteDao;

        insertAsyncTask(NoteDao dao) {
            asyncTaskNoteDao = dao;
        }

        @Override
        protected Void doInBackground(final TextNote... params) {
            Log.v("Alder", "REPO: Started adding note in background");
            asyncTaskNoteDao.insertNote(params[0]);
            return null;
        }
    }

    public void deleteNote(TextNote textNote) {

        new deleteAsyncTask(noteDao, notesList.getValue()).execute(textNote);

    }

    private static class deleteAsyncTask extends AsyncTask<TextNote, Void, Void> {
        private NoteDao asyncTaskNoteDao;
        private List<TextNote> textNotes;

        deleteAsyncTask(NoteDao dao, List<TextNote> textNotes) {
            asyncTaskNoteDao = dao;
            this.textNotes = textNotes;
        }

        @Override
        protected Void doInBackground(final TextNote... params) {
            asyncTaskNoteDao.deleteNote(params[0]);
            TextNote next = asyncTaskNoteDao.getNoteByPos(params[0].getPosition() + 1);
            if (next != null) {
                moveNotePosUp(next.getPosition());
            }
            return null;
        }

        private void moveNotePosUp(int firstItemPos) {
            TextNote current;
            for (int i = firstItemPos; i <= textNotes.size(); i++) {
                current = asyncTaskNoteDao.getNoteByPos(i);
                current.setPosition(i - 1);
                asyncTaskNoteDao.updateNote(current);
            }
        }
    }

    // TextNote moved up
    public void moveNote(TextNote holdingTextNote, TextNote destTextNote, ListViewModel model) {
        model.inProgress = true;
        synchronized (this) {
            new moveNoteAsyncTask(noteDao, holdingTextNote, destTextNote, model).execute();
        }
    }

    private static class moveNoteAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        private TextNote holdingTextNote;
        private TextNote destTextNote;
        private ListViewModel model;

        moveNoteAsyncTask(NoteDao noteDao, TextNote holdingTextNote, TextNote destTextNote, ListViewModel model) {
            this.noteDao = noteDao;
            this.holdingTextNote = holdingTextNote;
            this.destTextNote = destTextNote;
            this.model = model;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            int fromPosition = holdingTextNote.getPosition();
            int toPosition = destTextNote.getPosition();

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
            TextNote firstTextNote = noteDao.getNoteByPos(firstPos);
            TextNote secondTextNote = noteDao.getNoteByPos(secondPos);

            firstTextNote.setPosition(secondPos);
            secondTextNote.setPosition(firstPos);

            noteDao.updateNote(firstTextNote);
            noteDao.updateNote(secondTextNote);
        }
    }
}
