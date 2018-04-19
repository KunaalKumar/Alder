package com.ashiana.zlifno.alder.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

// Adds functionality for the database
@Dao
public interface NoteDao {

    @Insert(onConflict = IGNORE)
    void insertNote(TextNote textNote);

    @Query("DELETE FROM note_text_table")
    void deleteAll();

    @Update(onConflict = REPLACE)
    void updateNote(TextNote textNote);

    @Delete()
    void deleteNote(TextNote textNote);

    @Query("DELETE FROM note_text_table WHERE id = (:id)")
    void deleteNoteById(int id);

    @Query("SELECT * FROM note_text_table ORDER BY position ASC")
    LiveData<List<TextNote>> getAllNotes();

    @Query("SELECT * FROM note_text_table WHERE position = (:pos)")
    TextNote getNoteByPos(int pos);
}
