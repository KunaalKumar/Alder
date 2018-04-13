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
    void insertNote(Note note);

    @Query("DELETE FROM note_table")
    void deleteAll();

    @Update(onConflict = REPLACE)
    void updateNote(Note note);

    @Delete()
    void deleteNote(Note note);

    @Query("SELECT * FROM note_table")
    LiveData<List<Note>> getAllNotes();
}
