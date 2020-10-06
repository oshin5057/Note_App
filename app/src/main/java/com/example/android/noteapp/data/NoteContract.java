package com.example.android.noteapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Patterns;

public final class NoteContract {

    private NoteContract(){

    }

    public static final String CONTENT_AUTHORITY = "com.example.android.noteapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NOTES = "notes";

    public static final class NoteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_NOTES;

        public static final String TABLE_NAME = "notes";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_CONTACT = "contact";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_NOTE = "note";

        public static boolean isValidPhone(String contact){
            return Patterns.PHONE.matcher(contact).matches();
        }

    }
}
