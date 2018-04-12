package com.ashiana.zlifno.to_do;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashiana.zlifno.to_do.data.Note;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteListViewHolder> {

    class NoteListViewHolder extends RecyclerView.ViewHolder {
        private final TextView noteTitle;

        private NoteListViewHolder(View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.list_note_title);
        }
    }

    private final LayoutInflater inflater;
    private List<Note> notes;

    public NoteListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public NoteListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new NoteListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteListViewHolder holder, int position) {
        if (notes != null) {
            Note current = notes.get(position);
            holder.noteTitle.setText(current.getTitle());
        } else {
            // Covers the case of data not being ready yet.
            holder.noteTitle.setText("No Note");
        }
    }

    void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes != null ? notes.size() : 0;
    }
}


