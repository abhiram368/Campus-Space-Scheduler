package com.example.campus_space_scheduler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.campus_space_scheduler.databinding.AActivityDayScheduleBinding;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class DayScheduleActivity extends AppCompatActivity {

    private AActivityDayScheduleBinding binding;
    private SlotAdapter adapter;

    private List<Map<String, Object>> slotList = new ArrayList<>();

    private String spaceId;
    private String selectedDate;
    private boolean editMode;
    private boolean detailedMode;
    private String scheduleId;

    private DatabaseReference db;
    private DatabaseReference slotsRef;
    private ValueEventListener slotListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AActivityDayScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editMode = getIntent().getBooleanExtra("edit", false);
        detailedMode = getIntent().getBooleanExtra("detailed", false);

        if (editMode) detailedMode = true;

        spaceId = getIntent().getStringExtra("spaceId");
        long millis = getIntent().getLongExtra("dateMillis", -1);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(cal.getTime());

        scheduleId = spaceId + "_" + selectedDate;

        adapter = new SlotAdapter(slotList, editMode, detailedMode, this, scheduleId);
        binding.recyclerView.setAdapter(adapter);
        db = FirebaseDatabase.getInstance().getReference();

        DatabaseReference spaceRef = db.child("spaces").child(spaceId);

        spaceRef.child("roomName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String spaceName = snapshot.getValue(String.class);

                if (spaceName != null) {
                    binding.toolbar.setTitle(spaceName + " - " + selectedDate);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // initialize reference once
        slotsRef = db.child("schedules")
                .child(scheduleId)
                .child("slots");
    }

    @Override
    protected void onStart() {
        super.onStart();
        attachListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        detachListener();
    }

    private void attachListener() {

        if (slotListener != null) return; // prevent duplicate listeners

        slotListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                slotList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Map<String, Object> data =
                                (Map<String, Object>) ds.getValue();

                        if (data == null) continue;

                        // 🔥 critical: attach key
                        data.put("slotId", ds.getKey());

                        slotList.add(data);
                    }
                }

                sortSlots();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        };

        slotsRef.addValueEventListener(slotListener);
    }

    private void detachListener() {

        if (slotListener != null) {
            slotsRef.removeEventListener(slotListener);
            slotListener = null;
        }
    }

    private void sortSlots() {
        Collections.sort(slotList, (a, b) -> {

            String s1 = (String) a.get("start");
            String s2 = (String) b.get("start");

            if (s1 == null) s1 = "";
            if (s2 == null) s2 = "";

            return s1.compareTo(s2);
        });
    }
}