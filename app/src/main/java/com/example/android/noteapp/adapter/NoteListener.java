package com.example.android.noteapp.adapter;

public interface NoteListener {
    void onDelete(int position, int cursorId);
    void onEdit(int position, int cursorId);
}
