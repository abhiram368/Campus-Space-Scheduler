package com.example.campus_space_scheduler.ui;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeekScheduleAdapter extends RecyclerView.Adapter<WeekScheduleAdapter.VH> {

    private List<SlotCell> cells = new ArrayList<>();

    public void setData(List<SlotCell> data) {
        this.cells = data;
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(16, 16, 16, 16);
        return new VH(tv);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        SlotCell cell = cells.get(position);
        TextView tv = (TextView) holder.itemView;
        tv.setText(cell.time != null ? cell.time : "");

        if (cell.status == null) {
            tv.setBackgroundColor(Color.TRANSPARENT);
            return;
        }

        switch (cell.status) {
            case "AVAILABLE":
                tv.setBackgroundColor(Color.GREEN);
                break;
            case "BLOCKED":
                tv.setBackgroundColor(Color.GRAY);
                break;
            case "BOOKED":
                tv.setBackgroundColor(Color.RED);
                break;
            case "PENDING":
                tv.setBackgroundColor(Color.YELLOW);
                break;
            default:
                tv.setBackgroundColor(Color.WHITE);
                break;
        }
    }

    public void updateSlot(String date, String slotId, String status) {
        Calendar cal = Calendar.getInstance();
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
            cal.setTime(d);
        } catch (Exception e) {
            return;
        }

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // Map Calendar day to 0-6 index. 
        // Calendar.SUNDAY = 1, MONDAY = 2, ...
        // If we want 0 = Monday, 1 = Tuesday, ..., 6 = Sunday:
        int dayIndex = (dayOfWeek + 5) % 7;

        for (int i = 0; i < cells.size(); i++) {
            SlotCell cell = cells.get(i);
            if (cell.day == dayIndex && slotId.equals(cell.time)) {
                cell.status = status;
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        VH(TextView v) {
            super(v);
        }
    }
}
