package com.growfund.seedtowealth.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growfund.seedtowealth.R;
import com.growfund.seedtowealth.model.RandomEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<RandomEvent> events = new ArrayList<>();

    public void setEvents(List<RandomEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_notification, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        RandomEvent event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventIcon;
        private final TextView eventTitle;
        private final TextView eventDescription;
        private final TextView eventImpact;
        private final TextView eventSeverity;
        private final TextView eventDuration;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventIcon = itemView.findViewById(R.id.eventIcon);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            eventImpact = itemView.findViewById(R.id.eventImpact);
            eventSeverity = itemView.findViewById(R.id.eventSeverity);
            eventDuration = itemView.findViewById(R.id.eventDuration);
        }

        public void bind(RandomEvent event) {
            // Set icon
            eventIcon.setText(event.getEventIcon());

            // Set title (formatted event type)
            String title = formatEventType(event.getEventType());
            eventTitle.setText(title);

            // Set description
            eventDescription.setText(event.getDescription());

            // Set impact
            double multiplier = event.getImpactMultiplier();
            int impactPercent = (int) Math.round((multiplier - 1.0) * 100);
            String impactText = (impactPercent >= 0 ? "+" : "") + impactPercent + "%";
            eventImpact.setText(impactText);

            // Color based on positive/negative
            if (event.isPositiveEvent()) {
                eventImpact.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                eventImpact.setTextColor(Color.parseColor("#F44336")); // Red
            }

            // Set severity badge
            eventSeverity.setText(event.getSeverity());
            int severityColor = switch (event.getSeverity()) {
                case "LOW" -> Color.parseColor("#FFA726"); // Orange
                case "MEDIUM" -> Color.parseColor("#FF7043"); // Deep Orange
                case "HIGH" -> Color.parseColor("#E53935"); // Red
                default -> Color.GRAY;
            };
            eventSeverity.setBackgroundColor(severityColor);

            // Set duration
            try {
                LocalDateTime endTime = LocalDateTime.parse(event.getEndTime());
                LocalDateTime now = LocalDateTime.now();
                long hoursRemaining = ChronoUnit.HOURS.between(now, endTime);
                long daysRemaining = hoursRemaining / 24;

                String durationText;
                if (daysRemaining > 0) {
                    durationText = "Ends in " + daysRemaining + " day" + (daysRemaining > 1 ? "s" : "");
                } else if (hoursRemaining > 0) {
                    durationText = "Ends in " + hoursRemaining + " hour" + (hoursRemaining > 1 ? "s" : "");
                } else {
                    durationText = "Ending soon";
                }
                eventDuration.setText(durationText);
            } catch (Exception e) {
                eventDuration.setText("Active");
            }
        }

        private String formatEventType(String eventType) {
            return switch (eventType) {
                case RandomEvent.DROUGHT -> "Drought";
                case RandomEvent.PEST_ATTACK -> "Pest Attack";
                case RandomEvent.BONUS_RAIN -> "Bonus Rainfall";
                case RandomEvent.MARKET_SURGE -> "Market Surge";
                case RandomEvent.MARKET_CRASH -> "Market Crash";
                case RandomEvent.HEATWAVE -> "Heatwave";
                case RandomEvent.GOVERNMENT_SUBSIDY -> "Government Subsidy";
                default -> "Random Event";
            };
        }
    }
}
