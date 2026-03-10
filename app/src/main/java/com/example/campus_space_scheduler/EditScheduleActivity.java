package com.example.campus_space_scheduler;

public class EditScheduleActivity
        extends BaseScheduleSelectorActivity {

    @Override
    protected boolean editFlag() {
        return true;
    }

    @Override
    protected boolean detailedFlag() {
        return true;
    }
}