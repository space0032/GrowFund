package com.growfund.seedtowealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growfund.seedtowealth.R;
import com.growfund.seedtowealth.model.Crop;

import java.util.ArrayList;
import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    private List<Crop> crops = new ArrayList<>();
    private OnCropClickListener listener;

    public interface OnCropClickListener {
        void onCropClick(Crop crop);
    }

    public CropAdapter(OnCropClickListener listener) {
        this.listener = listener;
    }

    public void setCrops(List<Crop> crops) {
        this.crops = crops;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crop, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        Crop crop = crops.get(position);
        holder.bind(crop);
    }

    @Override
    public int getItemCount() {
        return crops.size();
    }

    class CropViewHolder extends RecyclerView.ViewHolder {
        private TextView cropIconText, cropTypeText, areaText, statusBadge, timerText;
        private androidx.cardview.widget.CardView iconCard;

        public CropViewHolder(@NonNull View itemView) {
            super(itemView);
            cropIconText = itemView.findViewById(R.id.cropIconText);
            cropTypeText = itemView.findViewById(R.id.cropTypeText);
            areaText = itemView.findViewById(R.id.areaText);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            timerText = itemView.findViewById(R.id.timerText);
            iconCard = (androidx.cardview.widget.CardView) cropIconText.getParent();

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCropClick(crops.get(position));
                }
            });
        }

        public void bind(Crop crop) {
            cropTypeText.setText(capitalize(crop.getCropType()));
            areaText.setText(String.format("%.1f ac", crop.getAreaPlanted()));

            // Icon
            String initial = crop.getCropType().length() > 0 ? crop.getCropType().substring(0, 1) : "?";
            cropIconText.setText(initial);

            // Status and Colors
            int statusColor;
            String statusLabel = crop.getStatus();

            switch (crop.getStatus()) {
                case "HARVESTED":
                    statusColor = 0xFF4CAF50; // Green
                    statusLabel = "Harvested";
                    break;
                case "PLANTED":
                    statusColor = 0xFF2196F3; // Blue
                    statusLabel = "Planted";
                    break;
                case "GROWING":
                    statusColor = 0xFFFF9800; // Orange
                    statusLabel = "Growing";
                    break;
                case "FAILED":
                    statusColor = 0xFFF44336; // Red
                    statusLabel = "Failed";
                    break;
                default:
                    statusColor = 0xFF9E9E9E; // Gray
            }

            statusBadge.setText(statusLabel);
            statusBadge.getBackground().setTint(statusColor);

            // Timer Logic
            if ("PLANTED".equals(crop.getStatus()) || "GROWING".equals(crop.getStatus())) {
                long time = crop.getTimeRemainingInMillis();
                if (time > 0) {
                    long minutes = (time / 1000) / 60;
                    long seconds = (time / 1000) % 60;
                    timerText.setVisibility(View.VISIBLE);
                    timerText.setText(String.format("Ready in %02d:%02d", minutes, seconds));
                } else {
                    timerText.setVisibility(View.VISIBLE);
                    timerText.setText("Ready to Harvest!");
                    timerText.setTextColor(0xFF4CAF50);
                    statusBadge.setText("Ready");
                    statusBadge.getBackground().setTint(0xFF4CAF50);
                }
            } else {
                timerText.setVisibility(View.GONE);
            }
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty())
                return str;
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }
    }
}
