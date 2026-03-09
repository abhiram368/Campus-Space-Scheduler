package com.example.campus_space_scheduler;

import android.graphics.Color;

import com.example.campus_space_scheduler.enums.SlotStatus;

public class SlotColorMapper {

    public static int getColor(SlotStatus status) {

        switch (status) {

            case AVAILABLE:
                return Color.parseColor("#4CAF50");

            case BOOKED:
                return Color.parseColor("#F44336");

            case PENDING:
                return Color.parseColor("#FF9800");

            case BLOCKED:
                return Color.parseColor("#9E9E9E");

            case MAINTENANCE:
                return Color.parseColor("#FFC107");

            default:
                return Color.WHITE;
        }
    }
}