package com.example.android.noteapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.android.noteapp.adapter.NoteListener;
import com.example.android.noteapp.data.NoteContract;
import com.example.android.noteapp.model.Note;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.noteapp.R.id.action_delete;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_NOTE_LOADER = 0;

    private Uri mCurrentNoteUri;

    private EditText mNameEditText;
    private EditText mContactEditText;
    private EditText mTitleEditText;
    private EditText mNoteEditText;

    private boolean mNoteHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNoteHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();

        if (mCurrentNoteUri == null){
            setTitle(getString(R.string.add_note));

            invalidateOptionsMenu();
        }
        else {
            setTitle(getString(R.string.edit_note));

            getSupportLoaderManager().initLoader(EXISTING_NOTE_LOADER, null,this);
        }

        mNameEditText = (EditText) findViewById(R.id.et_name);
        mContactEditText = (EditText) findViewById(R.id.et_contact);
        mTitleEditText = (EditText) findViewById(R.id.et_title);
        mNoteEditText = (EditText) findViewById(R.id.et_note);

        mNameEditText.setOnTouchListener(mTouchListener);
        mContactEditText.setOnTouchListener(mTouchListener);
        mTitleEditText.setOnTouchListener(mTouchListener);
        mNoteEditText.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentNoteUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                saveNote();
                finish();
                return true;
            case action_delete:
                deleteNote();
                return true;
            case android.R.id.home:
                if(mNoteHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteNote() {
        if (mCurrentNoteUri != null){
            int rowDeleted = getContentResolver().delete(mCurrentNoteUri, null, null);

            if (rowDeleted == 0){
                Toast.makeText(this, R.string.error_with_deleting_note, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void saveNote() {
        String name = mNameEditText.getText().toString().trim();
        String contact = mContactEditText.getText().toString().trim();
        String title = mTitleEditText.getText().toString().trim();
        String note = mNoteEditText.getText().toString().trim();

        if (mCurrentNoteUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(contact)
                && TextUtils.isEmpty(title) && TextUtils.isEmpty(note)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NAME, name);
        values.put(NoteContract.NoteEntry.COLUMN_CONTACT, contact);
        values.put(NoteContract.NoteEntry.COLUMN_TITLE, title);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE, note);

        if (mCurrentNoteUri == null){
            Uri newUri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, values);
            if (newUri == null){
                Toast.makeText(this, R.string.error_with_saving_notes, Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
            else {
                Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }
        else {
            int rowAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);
            if (rowAffected == 0){
                Toast.makeText(this, R.string.error_with_updatin_note, Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
            else {
                Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }
        finish();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {

        String[] projection = {
                NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_NAME,
                NoteContract.NoteEntry.COLUMN_CONTACT,
                NoteContract.NoteEntry.COLUMN_TITLE,
                NoteContract.NoteEntry.COLUMN_NOTE
        };

        return new CursorLoader(this,
                mCurrentNoteUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1){
            return;
        }

        if (cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NAME);
            int contactColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_CONTACT);
            int titleColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_TITLE);
            int noteColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE);

            String name = cursor.getString(nameColumnIndex);
            String contact = cursor.getString(contactColumnIndex);
            String title = cursor.getString(titleColumnIndex);
            String note = cursor.getString(noteColumnIndex);

            mNameEditText.setText(name);
            mContactEditText.setText(contact);
            mTitleEditText.setText(title);
            mNoteEditText.setText(note);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mNoteEditText.setText("");
        mContactEditText.setText("");
        mTitleEditText.setText("");
        mNoteEditText.setText("");

    }

}
