package com.example.campus_space_scheduler;

public class DetailedViewScheduleActivity
        extends BaseScheduleSelectorActivity {

    @Override
    protected boolean editFlag() {
        return false;
    }

    @Override
    protected boolean detailedFlag() {
        return true;
    }
}
