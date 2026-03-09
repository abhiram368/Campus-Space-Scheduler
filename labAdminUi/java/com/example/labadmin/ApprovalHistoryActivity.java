package com.example.labadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ApprovalHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_history);

        RecyclerView rv = findViewById(R.id.rvApprovalHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<HistoryItem> history = new ArrayList<>();
        history.add(new HistoryItem("Dr. Smith", "Main Hall | Oct 20 | Slot 1", "APPROVED", R.color.success_action, android.R.drawable.ic_input_add));
        history.add(new HistoryItem("Student Council", "Seminar Room | Oct 19 | Slot 3", "REJECTED", R.color.danger_action, android.R.drawable.ic_delete));
        history.add(new HistoryItem("Prof. Jones", "Lab 101 | Oct 18 | Slot 2", "FORWARDED", R.color.warning_action, android.R.drawable.ic_menu_send));
        history.add(new HistoryItem("Ms. Alice", "Auditorium | Oct 17 | Slot 1", "APPROVED", R.color.success_action, android.R.drawable.ic_input_add));
        history.add(new HistoryItem("Dr. Brown", "Main Hall | Oct 16 | Slot 2", "REJECTED", R.color.danger_action, android.R.drawable.ic_delete));

        rv.setAdapter(new HistoryAdapter(history));
    }

    static class HistoryItem {
        String name, details, statusText;
        int statusColor, iconRes;

        HistoryItem(String name, String details, String statusText, int statusColor, int iconRes) {
            this.name = name;
            this.details = details;
            this.statusText = statusText;
            this.statusColor = statusColor;
            this.iconRes = iconRes;
        }
    }

    static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private final List<HistoryItem> items;

        HistoryAdapter(List<HistoryItem> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approval_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HistoryItem item = items.get(position);
            holder.name.setText(item.name);
            holder.details.setText(item.details);
            holder.statusBadge.setText(item.statusText);
            
            int color = ContextCompat.getColor(holder.itemView.getContext(), item.statusColor);
            holder.statusBadge.setTextColor(color);
            holder.icon.setColorFilter(color);
            holder.icon.setImageResource(item.iconRes);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, details, statusBadge;
            ImageView icon;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.txtRequesterName);
                details = v.findViewById(R.id.txtHistoryDetails);
                statusBadge = v.findViewById(R.id.txtStatusBadge);
                icon = v.findViewById(R.id.imgStatusIcon);
            }
        }
    }
}