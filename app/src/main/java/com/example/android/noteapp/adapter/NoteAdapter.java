package com.example.android.noteapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.noteapp.R;
import com.example.android.noteapp.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder> {
    private List<Note> notes;
    private NoteListener listener;

    public NoteAdapter(List<Note> notes, NoteListener listener){
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.nameTextView.setText(notes.get(position).name);
        holder.titleTextView.setText(notes.get(position).note);
        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onEdit(position, notes.get(position).cursorId);
                }
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onDelete(position, notes.get(position).cursorId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setData(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView titleTextView;
        ImageButton ibEdit;
        ImageButton ibDelete;

        public Holder(@NonNull View view) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.tv_name_list);
            titleTextView = (TextView) view.findViewById(R.id.tv_note_list);
            ibDelete = (ImageButton) view.findViewById(R.id.ib_delete);
            ibEdit = (ImageButton) view.findViewById(R.id.ib_edit);
        }
    }
}
