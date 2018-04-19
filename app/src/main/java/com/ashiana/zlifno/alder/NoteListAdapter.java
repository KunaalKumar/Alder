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
import com.ashiana.zlifno.alder.data.TextNote;
import com.skyfishjy.library.RippleBackground;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteListAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private final LayoutInflater mInflater;
    private List<TextNote> mTextNotes; // Cached copy of notes
    private Context context;

    public NoteListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setNotes(List<TextNote> words) {
        mTextNotes = words;
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
        holder.currentItem = mTextNotes.get(position);
        holder.noteTitleView.setText(holder.currentItem.getTitle());
        holder.noteTimeCreatedView.setText(holder.currentItem.getTimeCreated());

        if (mTextNotes.get(position).getTitle().equals(ListFragment.isNewTitle) &&
                mTextNotes.get(position).getTimeCreated().equals(ListFragment.isNewTime)) {
            RippleBackground rippleBackground = holder.parent.findViewById(R.id.content);
            rippleBackground.startRippleAnimation();
            new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    rippleBackground.stopRippleAnimation();
                }

            }.start();

            ListFragment.isNewTitle = null;
            ListFragment.isNewTime = null;
        }

        holder.setItemClickListener((view, position1) -> {
            TextNote current = mTextNotes.get(position1);

            ListFragment.updateNote(current);
        });
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mTextNotes != null)
            return mTextNotes.size();
        else return 0;
    }


    public void deleteNote(int position) {
        notifyItemRemoved(position);
    }

    public TextNote getNote(int position) {
        return mTextNotes.get(position);
    }
}

class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.card_note_title)
    public TextView noteTitleView;
    @BindView(R.id.card_note_time_text)
    public TextView noteTimeCreatedView;

    public View parent;

    public TextNote currentItem;
    ItemClickListener itemClickListener;

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
//            TextNote note = mTextNotes.get(getAdapterPosition());
//            showSnackBar(note.getTitle());
        itemClickListener.onItemClick(v, getAdapterPosition());
    }

    public void setItemClickListener(ItemClickListener ic)

    {
        this.itemClickListener = ic;
    }
}