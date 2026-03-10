package com.example.labadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LiveStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_status);

        RecyclerView rv = findViewById(R.id.rvHallStatus);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<HallStatus> halls = new ArrayList<>();
        // Mentioning only 3 labs as requested: CSED Seminar Hall, Discussion Room, APJ Hall
        halls.add(new HallStatus("CSED Seminar Hall", true, "Dr. Smith", "Slot 1 (9-11 AM)", "Faculty"));
        halls.add(new HallStatus("Discussion Room", false, null, null, null));
        halls.add(new HallStatus("APJ Hall", true, "Prof. Jones", "Slot 2 (11-1 PM)", "Faculty"));

        rv.setAdapter(new HallAdapter(halls, hall -> {
            if (hall.isBooked) {
                Intent intent = new Intent(this, RequestDetailsActivity.class);
                intent.putExtra("bookedBy", hall.bookedBy);
                intent.putExtra("hallName", hall.name);
                intent.putExtra("date", "Today");
                intent.putExtra("slot", hall.slot);
                intent.putExtra("role", hall.role);
                intent.putExtra("hideButtons", true); // Tell details activity to hide buttons
                startActivity(intent);
            } else {
                Toast.makeText(this, hall.name + " is currently available", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    static class HallStatus {
        String name;
        boolean isBooked;
        String bookedBy, slot, role;

        HallStatus(String name, boolean isBooked, String bookedBy, String slot, String role) {
            this.name = name;
            this.isBooked = isBooked;
            this.bookedBy = bookedBy;
            this.slot = slot;
            this.role = role;
        }
    }

    static class HallAdapter extends RecyclerView.Adapter<HallAdapter.ViewHolder> {
        private final List<HallStatus> halls;
        private final OnClickListener listener;

        interface OnClickListener { void onClick(HallStatus hall); }

        HallAdapter(List<HallStatus> halls, OnClickListener listener) {
            this.halls = halls;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hall_status, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HallStatus hall = halls.get(position);
            holder.name.setText(hall.name);
            
            if (hall.isBooked) {
                holder.status.setText("Occupied");
                holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.danger_action));
                holder.icon.setImageResource(android.R.drawable.presence_busy);
                holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.danger_action));
            } else {
                holder.status.setText("Available");
                holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success_action));
                holder.icon.setImageResource(android.R.drawable.presence_online);
                holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.success_action));
            }

            holder.itemView.setOnClickListener(v -> listener.onClick(hall));
        }

        @Override
        public int getItemCount() { return halls.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, status;
            ImageView icon;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.txtHallName);
                status = v.findViewById(R.id.txtStatus);
                icon = v.findViewById(R.id.imgStatusIcon);
            }
        }
    }
}