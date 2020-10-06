package com.example.android.noteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.noteapp.adapter.NoteAdapter;
import com.example.android.noteapp.adapter.NoteListener;
import com.example.android.noteapp.data.NoteContract;
import com.example.android.noteapp.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener {

    private NoteAdapter mCursorAdapter;

    private List<Note> notes = new ArrayList<>();

    public static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        RecyclerView mListView = (RecyclerView) findViewById(R.id.list_view);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mCursorAdapter = new NoteAdapter(notes, this);
        mListView.setAdapter(mCursorAdapter);

        fetchAllNotes();
    }

    @Override
    public void onEdit(int position, int cursorId) {
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);

        Uri mCurrentNoteUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI, cursorId);

        intent.setData(mCurrentNoteUri);

        startActivityForResult(intent,100);
    }

    @Override
    public void onDelete(int position, int cursorId) {
        Uri mCurrentNoteUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI, cursorId);
        getContentResolver().delete(mCurrentNoteUri, null, null);
        notes.remove(position);
        mCursorAdapter.setData(notes);
    }

    private void fetchAllNotes() {

        Cursor cursor = null;

        String[] projection = {
                NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_NAME,
                NoteContract.NoteEntry.COLUMN_CONTACT,
                NoteContract.NoteEntry.COLUMN_TITLE,
                NoteContract.NoteEntry.COLUMN_NOTE
        };
        try {
            notes = new ArrayList<>();
            cursor = getContentResolver().query(NoteContract.NoteEntry.CONTENT_URI, projection, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                int idColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry._ID);
                int nameColumnIndex  = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NAME);
                int contactColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_CONTACT);
                int titleColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_TITLE);
                int noteColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE);

                int id = cursor.getInt(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                String contact = cursor.getString(contactColumnIndex);
                String title = cursor.getString(titleColumnIndex);
                String note = cursor.getString(noteColumnIndex);
                Note note1 = new Note();
                note1.name = name;
                note1.cursorId = id;
                note1.note = note;
                notes.add(note1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        mCursorAdapter.setData(notes);

    }

    private void deleteAllNotes() {
        getContentResolver().delete(NoteContract.NoteEntry.CONTENT_URI, null, null);
        fetchAllNotes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fetchAllNotes();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_all_note:
                deleteAllNotes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}