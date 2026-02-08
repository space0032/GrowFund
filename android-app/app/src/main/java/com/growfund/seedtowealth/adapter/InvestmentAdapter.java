package com.growfund.seedtowealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growfund.seedtowealth.R;
import com.growfund.seedtowealth.model.Investment;

import java.util.ArrayList;
import java.util.List;

public class InvestmentAdapter extends RecyclerView.Adapter<InvestmentAdapter.ViewHolder> {

    private List<Investment> investments = new ArrayList<>();

    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_investment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(investments.get(position));
    }

    @Override
    public int getItemCount() {
        return investments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView investmentName;
        TextView investmentType;
        TextView principalAmount;
        TextView currentValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            investmentName = itemView.findViewById(R.id.investmentName);
            investmentType = itemView.findViewById(R.id.investmentType);
            principalAmount = itemView.findViewById(R.id.principalAmount);
            currentValue = itemView.findViewById(R.id.currentValue);
        }

        public void bind(Investment investment) {
            investmentName.setText(investment.getSchemeName());
            investmentType.setText(investment.getInvestmentType());
            principalAmount.setText(
                    com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(investment.getPrincipalAmount()));

            if (investment.getCurrentValue() != null) {
                currentValue.setText(
                        com.growfund.seedtowealth.utils.MoneyUtils.formatCurrency(investment.getCurrentValue()));

                // Color logic: Green if profit, Red if loss (though usually grow)
                long principal = investment.getPrincipalAmount();
                if (investment.getCurrentValue() >= principal) {
                    currentValue.setTextColor(0xFF4CAF50); // Green
                } else {
                    currentValue.setTextColor(0xFFF44336); // Red
                }
            } else {
                currentValue.setText("Pending");
            }
        }
    }
}
