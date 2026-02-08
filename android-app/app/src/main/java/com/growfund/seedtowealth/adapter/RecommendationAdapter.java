package com.growfund.seedtowealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.growfund.seedtowealth.R;
import com.growfund.seedtowealth.model.Recommendation;
import java.util.ArrayList;
import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private List<Recommendation> recommendations = new ArrayList<>();

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(recommendations.get(position));
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView titleText, descriptionText, typeBadge, returnText, costText, riskText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImage);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            typeBadge = itemView.findViewById(R.id.typeBadge);
            returnText = itemView.findViewById(R.id.returnText);
            costText = itemView.findViewById(R.id.costText);
            riskText = itemView.findViewById(R.id.riskText);
        }

        public void bind(Recommendation rec) {
            titleText.setText(rec.getTitle());
            descriptionText.setText(rec.getDescription());
            typeBadge.setText(rec.getType());
            returnText.setText("+" + rec.getEstimatedReturn() + "% ROI");
            costText.setText(
                    "Est. Cost: " + com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(rec.getEstimatedCost()));
            riskText.setText("Risk: " + rec.getRiskLevel());

            if ("CROP".equals(rec.getCategory())) {
                iconImage.setImageResource(android.R.drawable.ic_dialog_email); // Placeholder
            } else if ("INVESTMENT".equals(rec.getCategory())) {
                iconImage.setImageResource(android.R.drawable.ic_dialog_alert); // Placeholder
            } else {
                iconImage.setImageResource(android.R.drawable.ic_dialog_info);
            }

            // Color coding for Risk
            int riskColor = android.graphics.Color.BLUE;
            if ("HIGH".equals(rec.getRiskLevel()))
                riskColor = android.graphics.Color.RED;
            else if ("MEDIUM".equals(rec.getRiskLevel()))
                riskColor = 0xFFFFA500; // Orange
            riskText.setTextColor(riskColor);

            // Color coding for Type Badge
            if ("SAFE".equals(rec.getType()))
                typeBadge.setBackgroundResource(R.drawable.bg_rounded_status); // Greenish
            // Add more logic if needed
        }
    }
}
