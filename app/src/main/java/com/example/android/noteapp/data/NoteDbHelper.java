package com.example.android.noteapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "notes.db";

    public static final int DATABASE_VERSION = 1;

    public NoteDbHelper(Context context){
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_NOTE_TABLE = "CREATE TABLE " + NoteContract.NoteEntry.TABLE_NAME + " ("
                + NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteContract.NoteEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + NoteContract.NoteEntry.COLUMN_CONTACT + " TEXT NOT NULL, "
                + NoteContract.NoteEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + NoteContract.NoteEntry.COLUMN_NOTE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
