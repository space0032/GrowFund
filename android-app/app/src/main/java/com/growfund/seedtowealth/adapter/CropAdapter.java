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
        private TextView cropTypeText, areaText, statusText, investmentText;

        public CropViewHolder(@NonNull View itemView) {
            super(itemView);
            cropTypeText = itemView.findViewById(R.id.cropTypeText);
            areaText = itemView.findViewById(R.id.areaText);
            statusText = itemView.findViewById(R.id.statusText);
            investmentText = itemView.findViewById(R.id.investmentText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCropClick(crops.get(position));
                }
            });
        }

        public void bind(Crop crop) {
            cropTypeText.setText(crop.getCropType());
            areaText.setText(String.format("%.1f acres", crop.getAreaPlanted()));
            statusText.setText(crop.getStatus());
            investmentText.setText(String.format("₹%,d", crop.getInvestmentAmount()));

            // Set status color
            int statusColor;
            switch (crop.getStatus()) {
                case "HARVESTED":
                    statusColor = 0xFF4CAF50; // Green
                    break;
                case "PLANTED":
                    statusColor = 0xFF2196F3; // Blue
                    break;
                case "FAILED":
                    statusColor = 0xFFF44336; // Red
                    break;
                default:
                    statusColor = 0xFF9E9E9E; // Gray
            }
            statusText.setTextColor(statusColor);

            if ("PLANTED".equals(crop.getStatus()) || "GROWING".equals(crop.getStatus())) {
                long time = crop.getTimeRemainingInMillis();
                if (time > 0) {
                    long minutes = (time / 1000) / 60;
                    long seconds = (time / 1000) % 60;
                    statusText.setText(String.format("%s (%02d:%02d)", crop.getStatus(), minutes, seconds));
                } else {
                    statusText.setText("READY TO HARVEST");
                    statusText.setTextColor(0xFF4CAF50); // Green
                }
            }
        }
    }
}
