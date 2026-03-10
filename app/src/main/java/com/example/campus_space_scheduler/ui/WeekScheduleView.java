package com.example.campus_space_scheduler.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeekScheduleView extends FrameLayout {

    private RecyclerView recyclerView;
    private WeekRowAdapter adapter;

    private boolean editable = false;
    private boolean showUserId = true;

    public WeekScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        recyclerView = new RecyclerView(context);

        GridLayoutManager manager =
                new GridLayoutManager(context, 7);

        recyclerView.setLayoutManager(manager);

        adapter = new WeekRowAdapter();
        recyclerView.setAdapter(adapter);

        addView(recyclerView);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setShowUserId(boolean showUserId) {
        this.showUserId = showUserId;
    }

    public void updateDay(String date, Map<String,Object> slots) {

        if (adapter == null) return;

        for (String slotId : slots.keySet()) {

            Map slot = (Map) slots.get(slotId);

            String status = (String) slot.get("status");

            adapter.updateSlot(date, slotId, status);
        }
    }
}
