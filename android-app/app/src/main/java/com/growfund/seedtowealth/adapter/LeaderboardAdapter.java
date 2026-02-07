package com.growfund.seedtowealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growfund.seedtowealth.R;
import com.growfund.seedtowealth.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<LeaderboardEntry> entries = new ArrayList<>();

    public void setEntries(List<LeaderboardEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(entries.get(position));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankText;
        TextView farmNameText;
        TextView ownerNameText;
        TextView scoreText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rankText);
            farmNameText = itemView.findViewById(R.id.farmNameText);
            ownerNameText = itemView.findViewById(R.id.ownerNameText);
            scoreText = itemView.findViewById(R.id.scoreText);
        }

        public void bind(LeaderboardEntry entry) {
            rankText.setText("#" + entry.getRank());
            farmNameText.setText(entry.getFarmName());
            ownerNameText.setText(entry.getOwnerName());
            scoreText.setText("₹" + String.format("%,d", entry.getSavings()));

            // Highlight top 3
            if (entry.getRank() == 1) {
                rankText.setTextColor(0xFFFFD700); // Gold
            } else if (entry.getRank() == 2) {
                rankText.setTextColor(0xFFC0C0C0); // Silver
            } else if (entry.getRank() == 3) {
                rankText.setTextColor(0xFFCD7F32); // Bronze
            } else {
                rankText.setTextColor(0xFF757575); // Grey
            }
        }
    }
}
