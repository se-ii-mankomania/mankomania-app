package com.example.mankomania.screens;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mankomania.R;

import java.util.List;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder> {

    private List<String> scores;

    public ScoresAdapter(List<String> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        String score = scores.get(position);
        holder.scoreTextView.setText(score);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public void updateScores(List<String> newScores) {
        this.scores = newScores;
        notifyDataSetChanged();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView scoreTextView;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }
    }
}
