package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<NoteList> notes;
    private Context context;

    private static final int[] BACKGROUND_COLORS = {
            R.color.color1,
            R.color.color2,
            R.color.color3,
            R.color.color4,
            R.color.color5,
            R.color.color6
    };

    public NoteAdapter(Context context, List<NoteList> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_addnote_adapter, parent, false);
        return new ViewHolder(view);
    }

    private int getBackgroundColor(int position) {

        return ContextCompat.getColor(context, BACKGROUND_COLORS[position % BACKGROUND_COLORS.length]);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteList noteList = notes.get(position);
        holder.titleTextView.setText(noteList.getTitle());
        holder.dateTextView.setText(noteList.getFormattedDate());
        int backgroundColor = getBackgroundColor(position);
        holder.layout_note.setBackgroundColor(backgroundColor);
        holder.layout_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goToDetailIntent = new Intent(context, AddnoteActivity.class);


                goToDetailIntent.putExtra("title", noteList.getTitle());
                goToDetailIntent.putExtra("body", noteList.getBody());
                goToDetailIntent.putExtra("formattedDate", noteList.getFormattedDate());


                context.startActivity(goToDetailIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;

        LinearLayout layout_note;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            layout_note = itemView.findViewById(R.id.layout_note);
        }
    }
}
