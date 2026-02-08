package com.growfund.seedtowealth.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growfund.seedtowealth.R;
import com.growfund.seedtowealth.model.Equipment;

import java.util.ArrayList;
import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private List<Equipment> equipmentList = new ArrayList<>();
    private List<Long> ownedEquipmentIds = new ArrayList<>();
    private OnEquipmentClickListener listener;

    public interface OnEquipmentClickListener {
        void onPurchaseClick(Equipment equipment);
    }

    public EquipmentAdapter(OnEquipmentClickListener listener) {
        this.listener = listener;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList != null ? equipmentList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOwnedEquipmentIds(List<Long> ownedIds) {
        this.ownedEquipmentIds = ownedIds != null ? ownedIds : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipment, parent, false);
        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
        Equipment equipment = equipmentList.get(position);
        holder.bind(equipment);
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    class EquipmentViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewIcon;
        private TextView textViewName;
        private TextView textViewTier;
        private TextView textViewDescription;
        private TextView textViewYieldBonus;
        private TextView textViewCostReduction;
        private TextView textViewDurability;
        private TextView textViewCost;
        private TextView textViewOwned;
        private Button buttonPurchase;
        private LinearLayout layoutYieldBonus;
        private LinearLayout layoutCostReduction;

        public EquipmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewIcon = itemView.findViewById(R.id.textViewIcon);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewTier = itemView.findViewById(R.id.textViewTier);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewYieldBonus = itemView.findViewById(R.id.textViewYieldBonus);
            textViewCostReduction = itemView.findViewById(R.id.textViewCostReduction);
            textViewDurability = itemView.findViewById(R.id.textViewDurability);
            textViewCost = itemView.findViewById(R.id.textViewCost);
            textViewOwned = itemView.findViewById(R.id.textViewOwned);
            buttonPurchase = itemView.findViewById(R.id.buttonPurchase);
            layoutYieldBonus = itemView.findViewById(R.id.layoutYieldBonus);
            layoutCostReduction = itemView.findViewById(R.id.layoutCostReduction);
        }

        public void bind(Equipment equipment) {
            // Set icon
            textViewIcon.setText(equipment.getIcon() != null ? equipment.getIcon() : "🛠️");

            // Set name and description
            textViewName.setText(equipment.getName());
            textViewDescription.setText(equipment.getDescription());

            // Set tier with color
            textViewTier.setText(equipment.getTier());
            setTierBackground(equipment.getTier());

            // Set bonuses
            if (equipment.getYieldBonus() != null && equipment.getYieldBonus() > 0) {
                layoutYieldBonus.setVisibility(View.VISIBLE);
                textViewYieldBonus.setText(equipment.getYieldBonusText());
            } else {
                layoutYieldBonus.setVisibility(View.GONE);
            }

            if (equipment.getCostReduction() != null && equipment.getCostReduction() > 0) {
                layoutCostReduction.setVisibility(View.VISIBLE);
                textViewCostReduction.setText(equipment.getCostReductionText());
            } else {
                layoutCostReduction.setVisibility(View.GONE);
            }

            // Set durability
            textViewDurability.setText(equipment.getMaxDurability() + " uses");

            // Set cost
            textViewCost.setText("₹" + String.format("%,d", equipment.getCost()));

            // Check if owned
            boolean isOwned = ownedEquipmentIds.contains(equipment.getId());
            if (isOwned) {
                textViewOwned.setVisibility(View.VISIBLE);
                buttonPurchase.setEnabled(false);
                buttonPurchase.setText("Owned");
                buttonPurchase.setAlpha(0.6f);
            } else {
                textViewOwned.setVisibility(View.GONE);
                buttonPurchase.setEnabled(true);
                buttonPurchase.setText("Purchase");
                buttonPurchase.setAlpha(1.0f);
            }

            // Purchase button click
            buttonPurchase.setOnClickListener(v -> {
                if (listener != null && !isOwned) {
                    listener.onPurchaseClick(equipment);
                }
            });
        }

        private void setTierBackground(String tier) {
            int backgroundColor;
            switch (tier.toUpperCase()) {
                case "PREMIUM":
                    backgroundColor = Color.parseColor("#9C27B0"); // Purple
                    break;
                case "ADVANCED":
                    backgroundColor = Color.parseColor("#2196F3"); // Blue
                    break;
                case "BASIC":
                default:
                    backgroundColor = Color.parseColor("#4CAF50"); // Green
                    break;
            }
            textViewTier.setBackgroundColor(backgroundColor);
        }
    }
}
