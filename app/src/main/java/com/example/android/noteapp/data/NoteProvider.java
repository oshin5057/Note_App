package com.example.android.noteapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NoteProvider extends ContentProvider {

    public static final String LOG_TAG = NoteProvider.class.getSimpleName();

    private NoteDbHelper mDbHelper;

    private static final int NOTES = 100;

    private static final int NOTE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTES, NOTES);
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTES + "/#", NOTE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new NoteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                cursor =database.query(NoteContract.NoteEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case NOTE_ID:
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor =database.query(NoteContract.NoteEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query UNKNOWN Uri " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return insertNote(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertNote(Uri uri, ContentValues values) {
        String name = values.getAsString(NoteContract.NoteEntry.COLUMN_NAME);
        if (name == null){
            throw new IllegalArgumentException("Notes require a name");
        }
        String  contact = values.getAsString(NoteContract.NoteEntry.COLUMN_CONTACT);
        if (contact == null && !NoteContract.NoteEntry.isValidPhone(contact)){
            throw new IllegalArgumentException("Phone no. should be valid");
        }
        String title = values.getAsString(NoteContract.NoteEntry.COLUMN_TITLE);
        if (title == null){
            throw new IllegalArgumentException("Title is required for notes");
        }
        String notes = values.getAsString(NoteContract.NoteEntry.COLUMN_NOTE);
        if (notes == null){
            throw new IllegalArgumentException("Descriptions are required for notes");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);

        if (id == -1){
            Log.e(LOG_TAG,"Failed to insert row now " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return updateNote(uri, contentValues, selection, selectionArgs);

            case NOTE_ID:
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs =new String[]{ String.valueOf(ContentUris.parseId(uri))};
                return updateNote(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateNote(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(NoteContract.NoteEntry.COLUMN_NAME)){
            String name = values.getAsString(NoteContract.NoteEntry.COLUMN_NAME);
            if (name == null){
                throw new IllegalArgumentException("Notes require name");
            }
        }

        if (values.containsKey(NoteContract.NoteEntry.COLUMN_CONTACT)){
            String contact = values.getAsString(NoteContract.NoteEntry.COLUMN_CONTACT);
            if (contact == null || !NoteContract.NoteEntry.isValidPhone(contact)){
                throw new IllegalArgumentException("Contact no. should bs valid");
            }
        }

        if (values.containsKey(NoteContract.NoteEntry.COLUMN_TITLE)){
            String title = values.getAsString(NoteContract.NoteEntry.COLUMN_TITLE);
            if (title == null){
                throw new IllegalArgumentException("Title is required for notes");
            }
        }

        if (values.containsKey(NoteContract.NoteEntry.COLUMN_NOTE)){
            String note = values.getAsString(NoteContract.NoteEntry.COLUMN_NOTE);
            if (note == null){
                throw new IllegalArgumentException("Description for the title is required");
            }
        }

        if (values.size() == 0){
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowUpdate = db.update(NoteContract.NoteEntry.TABLE_NAME, values,selection, selectionArgs);

        if (rowUpdate != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdate;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                rowDeleted = db.delete(NoteContract.NoteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = db.delete(NoteContract.NoteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case NOTES:
                return NoteContract.NoteEntry.CONTENT_LIST_TYPE;
            case NOTE_ID:
                return NoteContract.NoteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri + " with match " + match);
        }
    }
}
