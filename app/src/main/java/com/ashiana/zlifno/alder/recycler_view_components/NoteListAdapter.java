package com.ashiana.zlifno.alder.recycler_view_components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashiana.zlifno.alder.activity.ListFragment;
import com.ashiana.zlifno.alder.R;
import com.ashiana.zlifno.alder.data.Note;
import com.skyfishjy.library.RippleBackground;

import java.util.List;

import butterknife.ButterKnife;

public class NoteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mInflater;
    private Context context;
    private List<Note> mNotes; // Cached copy of notes

    public NoteListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        Note note = mNotes.get(position);
        if (note != null) {
            return note.noteType;
        }
        return 0;
    }

    public void setNotes(List<Note> words) {
        mNotes = words;
        Log.v("Alder", "Adapter: Item count is " + getItemCount());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        switch (viewType) {
            case Note.NOTE_TYPE_TEXT:
                itemView = mInflater.inflate(R.layout.text_note_card, parent, false);
                return new NoteTextViewHolder(itemView);
            case Note.NOTE_TYPE_IMAGE:
                itemView = mInflater.inflate(R.layout.image_note_card, parent, false);
                return new NoteImageViewHolder(itemView);
        }
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Note note = mNotes.get(position);

        switch (note.noteType) {
            case Note.NOTE_TYPE_TEXT:
                NoteTextViewHolder hold = (NoteTextViewHolder) holder;
                bindTextViewHolder(hold, position);
            case Note.NOTE_TYPE_IMAGE:
                //TODO: bind image view holder

        }

    }

    private void bindTextViewHolder(NoteTextViewHolder hold, int position) {
        hold.currentItem = mNotes.get(position);

        String title = hold.currentItem.title;
        if (title.length() > 20) {
            hold.noteTitleView.setSingleLine(false);
        }

        hold.noteTitleView.setText(hold.currentItem.title);
        hold.noteTimeCreatedView.setText(hold.currentItem.timeCreated);

        if (mNotes.equals(ListFragment.isNewNote)) {
            RippleBackground rippleBackground = hold.parent.findViewById(R.id.content);
            rippleBackground.startRippleAnimation();
            new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    rippleBackground.stopRippleAnimation();
                }

            }.start();
            ListFragment.isNewNote = null;
        }

        hold.setItemClickListener((view, position1) -> {

            Note current = mNotes.get(position1);
            ListFragment.updateNote(current, context, hold.noteTitleView, hold.noteTimeCreatedView);
        });

    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mNotes != null)
            return mNotes.size();
        else return 0;
    }


    public void deleteNote(int position) {
        notifyItemRemoved(position);
    }

    public Note getNote(int position) {
        return mNotes.get(position);
    }

    class NoteTextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView noteTitleView;
        public TextView noteTimeCreatedView;

        ItemClickListener itemClickListener;

        public View parent;

        public Note currentItem;

        public NoteTextViewHolder(View itemView) {
            super(itemView);

            parent = itemView;
            noteTitleView = itemView.findViewById(R.id.card_note_title);
            noteTitleView.setInputType(0);

            noteTimeCreatedView = itemView.findViewById(R.id.card_note_time_text);
            noteTimeCreatedView.setInputType(0);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }
    }

    class NoteImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView imageTitle;

        ItemClickListener itemClickListener;

        public View parent;

        public NoteImageViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(itemView);

            parent = itemView;
            imageView = itemView.findViewById(R.id.card_image);
            imageTitle = itemView.findViewById(R.id.card_image_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            itemClickListener.onItemClick(v, getAdapterPosition());
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }

    }
}