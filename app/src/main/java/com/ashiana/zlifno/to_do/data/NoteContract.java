package com.ashiana.zlifno.to_do.data;

import android.provider.BaseColumns;

public final class NoteContract {
    public NoteContract() {
    }

    public static abstract class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_TIME_CREATED = "time_created";
    }
}
