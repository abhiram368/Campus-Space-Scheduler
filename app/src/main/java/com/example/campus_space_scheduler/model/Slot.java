package com.example.campus_space_scheduler.model;

import com.example.campus_space_scheduler.enums.SlotStatus;

public class Slot {

    public String slotId;
    public long startTime;
    public long endTime;

    public SlotStatus status;

    public String bookingId;
    public String userId;

    public Slot() {}
}
