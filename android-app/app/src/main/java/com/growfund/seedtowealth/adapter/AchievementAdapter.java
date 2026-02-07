package com.growfund.seedtowealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growfund.seedtowealth.R;
import com.growfund.seedtowealth.model.Achievement;

import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {

    private List<Achievement> achievements = new ArrayList<>();

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.titleText.setText(achievement.getTitle());
        holder.descriptionText.setText(achievement.getDescription());
        holder.iconText.setText(achievement.getIcon() != null ? achievement.getIcon() : "🏆");
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        TextView iconText, titleText, descriptionText;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            iconText = itemView.findViewById(R.id.iconText);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
        }
    }
}
