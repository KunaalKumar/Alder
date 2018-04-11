package com.ashiana.zlifno.to_do.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ashiana.zlifno.to_do.Note;
import com.ashiana.zlifno.to_do.data.NoteContract.NoteEntry;

public class NoteDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "to_do.db";
    private static final int DATABASE_VERSION = 1;

    public NoteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_NOTES_TABLE =  "CREATE TABLE " + NoteEntry.TABLE_NAME + "("
                + NoteEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, "
                + NoteEntry.COLUMN_NAME_CONTENT + " TEXT, "
                + NoteEntry.COLUMN_NAME_TIME_CREATED + " DATATIME NOT NULL); ";

        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
