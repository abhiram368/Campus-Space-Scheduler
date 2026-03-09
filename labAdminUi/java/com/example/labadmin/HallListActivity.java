package com.example.labadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HallListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_list);

        RecyclerView rv = findViewById(R.id.rvHalls);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<HallInfo> halls = new ArrayList<>();
        halls.add(new HallInfo("CSED Seminar Hall", "150", "CSED Block, 2nd Floor", "160", "2", "30", "20"));
        halls.add(new HallInfo("Discussion Room", "20", "Main Library, Ground Floor", "25", "5", "5", "10"));
        halls.add(new HallInfo("APJ Hall", "250", "Admin Block, 1st Floor", "260", "0", "40", "30"));

        rv.setAdapter(new HallAdapter(halls, hall -> {
            Intent intent = new Intent(this, HallDetailsActivity.class);
            intent.putExtra("hall", hall);
            startActivity(intent);
        }));
    }

    public static class HallInfo implements Serializable {
        String name, capacity, address, chairs, computers, tables, plugBoards;

        HallInfo(String name, String capacity, String address, String chairs, String computers, String tables, String plugBoards) {
            this.name = name;
            this.capacity = capacity;
            this.address = address;
            this.chairs = chairs;
            this.computers = computers;
            this.tables = tables;
            this.plugBoards = plugBoards;
        }
    }

    static class HallAdapter extends RecyclerView.Adapter<HallAdapter.ViewHolder> {
        private final List<HallInfo> halls;
        private final OnClickListener listener;

        interface OnClickListener { void onClick(HallInfo hall); }

        HallAdapter(List<HallInfo> halls, OnClickListener listener) {
            this.halls = halls;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hall, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HallInfo hall = halls.get(position);
            holder.name.setText(hall.name);
            holder.itemView.setOnClickListener(v -> listener.onClick(hall));
        }

        @Override
        public int getItemCount() { return halls.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.txtHallName);
            }
        }
    }
}