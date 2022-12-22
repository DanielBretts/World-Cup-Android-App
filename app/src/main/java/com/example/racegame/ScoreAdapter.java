package com.example.racegame;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Map;

//View holder is connecting between specific attributes in our view
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private Context context;
    private ArrayList<ScoreRecord> scores;
    private final int TOP_NUMBER = 10;
    private MapCallback mapCallback;

    public ScoreAdapter(Context context, ArrayList<ScoreRecord> scores) {
          this.context = context;
          this.scores = scores;
    }

    public void setMapCallback(MapCallback mapCallback){
        this.mapCallback = mapCallback;
    }

    @NonNull
    @Override
    public ScoreAdapter.ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //the parent is the view that holds the items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score,parent,false);
        ScoreViewHolder scoreViewHolder = new ScoreViewHolder(view);
        return scoreViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreAdapter.ScoreViewHolder holder, int position) {
        ScoreRecord score = scores.get(position);
        holder.scores_TXT_name.setText(score.getName());
        holder.scores_TXT_score.setText(String.valueOf(score.getScore()));
    }

    @Override
    public int getItemCount() {
        return scores == null ? 0 : scores.size();
    }

    public ScoreRecord getItem(int position){
        return scores.get(position);
    }

    public class ScoreViewHolder extends RecyclerView.ViewHolder{

        private MaterialTextView scores_TXT_name;
        private MaterialTextView scores_TXT_score;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            scores_TXT_name = itemView.findViewById(R.id.scores_TXT_name);
            scores_TXT_score = itemView.findViewById(R.id.scores_TXT_score);
            itemView.setOnClickListener(v -> {
                mapCallback.showLocation(getItem(getAdapterPosition()),getAdapterPosition());
            });
        }
    }
}
