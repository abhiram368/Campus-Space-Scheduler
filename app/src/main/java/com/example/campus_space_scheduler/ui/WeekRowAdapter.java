package com.example.campus_space_scheduler.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space_scheduler.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeekRowAdapter extends RecyclerView.Adapter<WeekRowAdapter.VH> {

    private List<Row> rows = new ArrayList<>();

    public WeekRowAdapter() {

        int hour = 8;
        int minute = 0;

        for (int i = 0; i < 30; i++) {

            rows.add(new Row(
                    String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            ));

            minute += 30;

            if (minute == 60) {
                minute = 0;
                hour++;
            }
        }
    }

    public void updateSlot(String date, String slotId, String status) {

        int row = timeToRow(slotId);

        Calendar cal = Calendar.getInstance();

        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
            cal.setTime(d);
        } catch (Exception e) {
            return;
        }

        int day = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if (day < 0) day = 6;

        rows.get(row).status[day] = status;

        notifyItemChanged(row);
    }

    private int timeToRow(String slotId) {

        int hour = Integer.parseInt(slotId.substring(0,2));
        int minute = Integer.parseInt(slotId.substring(2));

        int base = (hour - 8) * 2;

        if (minute == 30) base++;

        return base;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_row, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        Row row = rows.get(position);

        holder.timeLabel.setText(row.time);

        holder.daySlots.removeAllViews();

        for (int i = 0; i < 7; i++) {

            TextView cell = new TextView(holder.itemView.getContext());

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            1
                    );

            cell.setLayoutParams(p);

            String status = row.status[i];

            if ("AVAILABLE".equals(status))
                cell.setBackgroundColor(Color.GREEN);
            else if ("BOOKED".equals(status))
                cell.setBackgroundColor(Color.RED);
            else if ("PENDING".equals(status))
                cell.setBackgroundColor(Color.YELLOW);
            else if ("BLOCKED".equals(status))
                cell.setBackgroundColor(Color.GRAY);
            else
                cell.setBackgroundColor(Color.DKGRAY);

            holder.daySlots.addView(cell);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class Row {

        String time;
        String[] status = new String[7];

        Row(String t) {
            time = t;
        }
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView timeLabel;
        LinearLayout daySlots;

        VH(View v) {
            super(v);

            timeLabel = v.findViewById(R.id.timeLabel);
            daySlots = v.findViewById(R.id.daySlots);
        }
    }
}