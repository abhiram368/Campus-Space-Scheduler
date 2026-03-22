package com.example.campus_space_scheduler.app_admin;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space_scheduler.DetailedViewScheduleActivity;
import com.example.campus_space_scheduler.EditScheduleActivity;
import com.example.campus_space_scheduler.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.color.MaterialColors;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.campus_space_scheduler.enums.SlotStatus;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScheduleFragment extends Fragment {

    private Button uploadJsonBtn;
    private Button viewScheduleBtn;
    private Button editScheduleBtn;

    private ActivityResultLauncher<String> jsonPicker;
    private DatabaseReference db;
    private final List<ValueEventListener> slotListeners = new ArrayList<>();

    private String currentSpaceId;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.a_fragment_schedule, container, false);

        db = FirebaseDatabase.getInstance().getReference();

        uploadJsonBtn = view.findViewById(R.id.uploadJsonBtn);
        viewScheduleBtn = view.findViewById(R.id.viewScheduleBtn);
        editScheduleBtn = view.findViewById(R.id.editScheduleBtn);

        TextView header = view.findViewById(R.id.currentSpaceHeader);
        header.setOnClickListener(v -> openSpacePicker());
        TableLayout table = view.findViewById(R.id.weekPreview);

        loadCurrentSpace(view, table, header);

        jsonPicker =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                readJson(uri);
                            }
                        });

        setupButtons();

        return view;
    }

    private void setupButtons() {
        uploadJsonBtn.setOnClickListener(v -> jsonPicker.launch("*/*"));

        viewScheduleBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DetailedViewScheduleActivity.class));
        });

        editScheduleBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), EditScheduleActivity.class));
        });
    }

    private void loadCurrentSpace(View view, TableLayout table, TextView header) {

        db.child("appConfig").child("currentSpaceId")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            header.setText("No Space Selected");
                            header.setTextColor(Color.RED);
                            table.setVisibility(View.GONE);
                            return;
                        }

                        currentSpaceId = snapshot.getValue(String.class);
                        loadRoomNameAndDate(view, table, header);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("SCHEDULE", "Failed", error.toException());
                    }
                });
    }

    private void openSpacePicker() {

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_space_picker, null);

        RecyclerView recycler = view.findViewById(R.id.spaceList);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> names = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        db.child("spaces").get().addOnSuccessListener(snapshot -> {

            for (DataSnapshot space : snapshot.getChildren()) {
                String id = space.getKey();
                String name = space.child("roomName").getValue(String.class);

                if (id != null && name != null) {
                    ids.add(id);
                    names.add(name.trim());
                }
            }

            recycler.setAdapter(new RecyclerView.Adapter<>() {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    TextView tv = new TextView(parent.getContext());
                    tv.setPadding(32, 32, 32, 32);
                    tv.setTextSize(16);
                    return new RecyclerView.ViewHolder(tv) {};
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    TextView tv = (TextView) holder.itemView;
                    tv.setText(names.get(position));

                    tv.setOnClickListener(v -> {
                        String selectedId = ids.get(position);

                        db.child("appConfig").child("currentSpaceId")
                                .setValue(selectedId);

                        dialog.dismiss();
                    });
                }

                @Override
                public int getItemCount() {
                    return names.size();
                }
            });

        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void loadRoomNameAndDate(View view, TableLayout table, TextView header) {

        db.child("spaces").child(currentSpaceId).child("roomName").get()
                .addOnSuccessListener(snapshot -> {

                    if (!isAdded()) return;

                    String roomName = snapshot.getValue(String.class);
                    if (roomName == null) roomName = "Unknown Room";

                    String today = new SimpleDateFormat(
                            "EEEE, dd MMM yyyy", Locale.getDefault()
                    ).format(Calendar.getInstance().getTime());

                    header.setText(roomName + " • " + today);

                    int color = MaterialColors.getColor(
                            requireContext(),
                            com.google.android.material.R.attr.colorOnSurface,
                            Color.BLACK
                    );
                    header.setTextColor(color);

                    table.setVisibility(View.VISIBLE);

                    populateGrid(view, table, currentSpaceId);
                    loadWeekFromFirebase(currentSpaceId, table);
                });
    }

    private void readJson(Uri uri) {
        try {
            InputStream inputStream =
                    requireContext().getContentResolver().openInputStream(uri);

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();
            assert inputStream != null;
            inputStream.close();

            String json = builder.toString();

            new Thread(() -> {
                parseSchedule(json);
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseSchedule(String jsonString) {
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONObject schedules = root.getJSONObject("schedules");

            Iterator<String> spaceIds = schedules.keys();

            while (spaceIds.hasNext()) {
                String spaceId = spaceIds.next();
                JSONObject spaceSchedules = schedules.getJSONObject(spaceId);

                Iterator<String> dates = spaceSchedules.keys();

                while (dates.hasNext()) {
                    String date = dates.next();
                    JSONObject daySchedule = spaceSchedules.getJSONObject(date);
                    uploadSchedule(spaceId, date, daySchedule);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadSchedule(String spaceId, String date, JSONObject daySchedule)
            throws JSONException {

        String scheduleId = spaceId + "_" + date;

        Map<String, Object> scheduleDoc = new HashMap<>();
        scheduleDoc.put("spaceId", spaceId);
        scheduleDoc.put("date", date);

        JSONArray slots = daySchedule.getJSONArray("slots");
        Map<String, Object> slotsMap = new HashMap<>();

        for (int i = 0; i < slots.length(); i++) {
            JSONObject slot = slots.getJSONObject(i);

            Map<String, Object> slotDoc = new HashMap<>();
            slotDoc.put("start", slot.getString("start"));
            slotDoc.put("end", slot.getString("end"));
            slotDoc.put("status", slot.getString("status"));

            String slotId = slot.getString("start").replace(":", "");
            slotsMap.put(slotId, slotDoc);
        }

        scheduleDoc.put("slots", slotsMap);

        db.child("schedules")
                .child(scheduleId)
                .setValue(scheduleDoc)
                .addOnSuccessListener(aVoid ->
                        Log.d("SCHEDULE", "Uploaded: " + scheduleId))
                .addOnFailureListener(e ->
                        Log.e("SCHEDULE", "Write failed", e));
    }

    private void loadWeekFromFirebase(String spaceId, TableLayout table) {

        clearListeners();

        List<String> weekDates = getCurrentWeekDates();

        for (int j = 0; j < weekDates.size(); j++) {

            String date = weekDates.get(j);
            int dayIndex = j;
            String scheduleId = spaceId + "_" + date;

            DatabaseReference ref = db.child("schedules")
                    .child(scheduleId)
                    .child("slots");

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (!isAdded()) return;

                    if (snapshot.exists()) {
                        for (DataSnapshot slotSnapshot : snapshot.getChildren()) {

                            String start = slotSnapshot.child("start").getValue(String.class);
                            String status = slotSnapshot.child("status").getValue(String.class);

                            if (start != null && status != null) {
                                int slotIndex = calculateSlotIndex(start);
                                updateSlotColor(table, dayIndex, slotIndex, status);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("SCHEDULE", "Realtime failed", error.toException());
                }
            };

            ref.addValueEventListener(listener);
            slotListeners.add(listener);
        }
    }

    private void clearListeners() {

        if (currentSpaceId == null) return;

        List<String> weekDates = getCurrentWeekDates();

        for (int j = 0; j < weekDates.size(); j++) {

            String date = weekDates.get(j);
            String scheduleId = currentSpaceId + "_" + date;

            DatabaseReference ref = db.child("schedules")
                    .child(scheduleId)
                    .child("slots");

            if (j < slotListeners.size()) {
                ref.removeEventListener(slotListeners.get(j));
            }
        }

        slotListeners.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearListeners();
    }

    private int calculateSlotIndex(String startTime) {
        String[] parts = startTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return (hour - 8) * 2 + (minute >= 30 ? 1 : 0);
    }

    private void populateGrid(View rootView, TableLayout table, String spaceId) {

        if (!isAdded() || getContext() == null) {
            return; // Fragment is detached, stop trying to update UI
        }

        table.removeAllViews();

        String[] days = {"M", "T", "W", "Th", "F", "Sa", "S"};

        // 1. Column Headings (Days)
        TableRow headerRow = new TableRow(getContext());
        headerRow.addView(createCell("Time", true));
        for (String day : days) {
            headerRow.addView(createCell(day, true));
        }
        table.addView(headerRow);

        // 2. Row Headings (Times) and Data
        for (int i = 0; i < 30; i++) {
            TableRow row = new TableRow(getContext());

            int h1 = 8 + (i / 2); String m1 = (i % 2 == 0) ? "00" : "30";
            int h2 = 8 + ((i + 1) / 2); String m2 = ((i + 1) % 2 == 0) ? "00" : "30";
            String timeRange = h1 + ":" + m1 + "\n" + h2 + ":" + m2;

            row.addView(createCell(timeRange, true));

            for (int j = 0; j < 7; j++) {
                View cell = new View(getContext());
                // Weight 1f allows horizontal stretching to match screen width
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, 160, 1f);
                params.setMargins(1, 1, 1, 1); // Border effect
                cell.setLayoutParams(params);

                // ---------------------------------------------------------
                // UI COLOR CONFIGURATION: Default empty slot background
                // ---------------------------------------------------------
                cell.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_cell));
                cell.setTag(j + "_" + i);
                cell.setElevation(6f);
                row.addView(cell);
            }
            table.addView(row);
        }
    }

    private TextView createCell(String text, boolean isHeader) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_header));
        tv.setPadding(12, 16, 12, 16);
        tv.setTextSize(12f);
        tv.setElevation(4f);
        // ---------------------------------------------------------
        // UI COLOR CONFIGURATION: Background and text for headers (Days/Times)
        // ---------------------------------------------------------
        String tableHeaderBgHex = "#F5F5F5";
        int tableHeaderBackgroundColor = Color.parseColor(tableHeaderBgHex);
        int tableCellDefaultColor = Color.WHITE;

        tv.setBackgroundColor(isHeader ? tableHeaderBackgroundColor : tableCellDefaultColor);
        if (isHeader) tv.setTypeface(null, android.graphics.Typeface.BOLD);

        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
        params.setMargins(3, 3, 3, 3);
        tv.setLayoutParams(params);
        return tv;
    }

    private List<String> getCurrentWeekDates() {
        List<String> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        // Start at the first day of the week (usually Monday or Sunday depending on Locale)
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            dates.add(sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private void updateSlotColor(TableLayout table, int dayIndex, int slotIndex, String statusString) {
        View dot = table.findViewWithTag(dayIndex + "_" + slotIndex);
        if (dot != null) {
            try {
                // Converts String (e.g., "AVAILABLE") to SlotStatus Enum
                SlotStatus status = SlotStatus.valueOf(statusString.toUpperCase());
                // Gets color from your Mapper
                if (status == SlotStatus.AVAILABLE) {
                    dot.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.slot_available));
                } else if (status == SlotStatus.BOOKED) {
                    dot.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.slot_booked));
                } else if (status == SlotStatus.PENDING) {
                    dot.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.slot_pending));
                } else if (status == SlotStatus.BLOCKED) {
                    dot.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.slot_blocked));
                } else if (status == SlotStatus.MAINTENANCE) {
                    dot.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.slot_maintenance));
                }
            } catch (IllegalArgumentException e) {
                // ---------------------------------------------------------
                // UI COLOR CONFIGURATION: Color used when the slot status is unknown
                // ---------------------------------------------------------
                int unknownStatusFallbackColor = Color.LTGRAY;
                dot.setBackgroundColor(unknownStatusFallbackColor);
            }
        }
    }
}