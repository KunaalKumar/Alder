package com.ashiana.zlifno.to_do;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private ArrayList<Note> notes;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotesAdapter(ArrayList<Note> notes) {
        this.notes = notes;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // Create standard not view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView note = holder.view.findViewById(R.id.note_content);
        note.setText(notes.get(position).getContent());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return notes.size();
    }
}
