package com.example.labadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ViewScheduleActivity extends AppCompatActivity {

    private TextView txtSelectedDate;
    private SlotAdapter adapter;
    private List<SlotInfo> currentSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        CalendarView calendarView = findViewById(R.id.calendarView);
        RecyclerView rvSlots = findViewById(R.id.rvSlots);

        // Set date range for 6 months
        Calendar calendar = Calendar.getInstance();
        calendarView.setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, 6);
        calendarView.setMaxDate(calendar.getTimeInMillis());

        rvSlots.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SlotAdapter(currentSlots);
        rvSlots.setAdapter(adapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            txtSelectedDate.setText("Schedule for " + date);
            updateSlots(date);
        });

        // Initialize with today's slots
        Calendar now = Calendar.getInstance();
        updateSlots(now.get(Calendar.DAY_OF_MONTH) + "/" + (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.YEAR));
    }

    private void updateSlots(String date) {
        currentSlots.clear();
        Random random = new Random(date.hashCode()); // For deterministic mock data per date
        
        String[] times = {"09:00 AM - 11:00 AM", "11:00 AM - 01:00 PM", "02:00 PM - 04:00 PM", "04:00 PM - 06:00 PM"};
        
        for (String time : times) {
            boolean isBooked = random.nextBoolean();
            currentSlots.add(new SlotInfo(time, isBooked));
        }
        adapter.notifyDataSetChanged();
    }

    static class SlotInfo {
        String time;
        boolean isBooked;
        SlotInfo(String t, boolean b) { time = t; isBooked = b; }
    }

    static class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.ViewHolder> {
        private final List<SlotInfo> slots;

        SlotAdapter(List<SlotInfo> slots) { this.slots = slots; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SlotInfo slot = slots.get(position);
            holder.time.setText(slot.time);
            if (slot.isBooked) {
                holder.status.setText("BOOKED");
                holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.danger_action));
                holder.status.setBackgroundResource(R.drawable.bg_danger_action);
            } else {
                holder.status.setText("FREE");
                holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success_action));
                holder.status.setBackgroundResource(R.drawable.bg_secondary_action);
            }
        }

        @Override
        public int getItemCount() { return slots.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView time, status;
            ViewHolder(View v) {
                super(v);
                time = v.findViewById(R.id.txtSlotTime);
                status = v.findViewById(R.id.txtSlotStatus);
            }
        }
    }
}