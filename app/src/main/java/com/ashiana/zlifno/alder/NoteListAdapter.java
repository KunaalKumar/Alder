package com.ashiana.zlifno.alder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashiana.zlifno.alder.Fragment.ListFragment;
import com.ashiana.zlifno.alder.data.Note;
import com.skyfishjy.library.RippleBackground;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    private final LayoutInflater mInflater;
    private List<Note> mNotes; // Cached copy of notes

    public NoteListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setNotes(List<Note> words) {
        mNotes = words;
        Log.v("Alder", "Adapter: Item count is " + getItemCount());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.text_note_card, parent, false);
        return new NoteViewHolder(itemView);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        holder.currentItem = mNotes.get(position);

        String title = holder.currentItem.title;
        if (title.length() > 20) {
            holder.noteTitleView.setSingleLine(false);
        }

        holder.noteTitleView.setText(holder.currentItem.title);
        holder.noteTimeCreatedView.setText(holder.currentItem.timeCreated);

        holder.noteTitleView.setTransitionName("transition" + position);

        if (mNotes.equals(ListFragment.isNewNote)) {
            RippleBackground rippleBackground = holder.parent.findViewById(R.id.content);
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

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.card_note_title)
        public TextView noteTitleView;
        @BindView(R.id.card_note_time_text)
        public TextView noteTimeCreatedView;

        public View parent;

        public Note currentItem;

        public NoteViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(itemView);

            parent = itemView;
            noteTitleView = itemView.findViewById(R.id.card_note_title);
            noteTitleView.setInputType(0);

            noteTimeCreatedView = itemView.findViewById(R.id.card_note_time_text);
            noteTimeCreatedView.setInputType(0);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Note current = mNotes.get(getAdapterPosition());

            ListFragment.updateNote(current, getAdapterPosition(), v);

        }

    }
}