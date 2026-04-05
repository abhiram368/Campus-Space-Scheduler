package com.example.campus_space_scheduler.booking_user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_space_scheduler.R;
import com.example.hod.firebase.FirebaseRepository;
import com.example.hod.utils.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.google.firebase.database.ValueEventListener;

public class BookingFormActivity extends AppCompatActivity {

    private static final String TAG = "BookingFormActivity";
    private String slotStart, date, timeSlot, spaceName, scheduleId, spaceId, spaceType, userRole;
    private EditText etPurpose, etDescription, etLorUrl;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_activity_booking_form);

        // Get data from Intent
        slotStart = getIntent().getStringExtra("SLOT_START");
        date = getIntent().getStringExtra("DATE");
        timeSlot = getIntent().getStringExtra("TIME_SLOT");
        spaceName = getIntent().getStringExtra("SPACE_NAME");
        scheduleId = getIntent().getStringExtra("SCHEDULE_ID");
        spaceId = getIntent().getStringExtra("SPACE_ID");
        spaceType = getIntent().getStringExtra("SPACE_TYPE");
        userRole = getIntent().getStringExtra("ROLE");

        // UI components
        TextView tvDetails = findViewById(R.id.textViewBookingDetails);
        etPurpose = findViewById(R.id.editTextPurpose);
        etDescription = findViewById(R.id.editTextDescription);
        etLorUrl = findViewById(R.id.editTextLorUrl);
        buttonSubmit = findViewById(R.id.buttonSubmitBooking);

        tvDetails.setText(String.format("%s\nDate: %s\nTime: %s", spaceName, date, timeSlot));

        buttonSubmit.setOnClickListener(v -> submitBooking());
    }

    private void submitBooking() {
        String purpose = etPurpose.getText() != null ? etPurpose.getText().toString().trim() : "";
        if (purpose.isEmpty()) {
            etPurpose.setError("Purpose is required");
            return;
        }

        buttonSubmit.setEnabled(false);
        checkAvailabilityAndSubmit();
    }

    private void checkAvailabilityAndSubmit() {
        DatabaseReference slotRef = FirebaseDatabase.getInstance().getReference("schedules")
                .child(scheduleId).child("slots");

        boolean isClassroom = spaceType != null && spaceType.equalsIgnoreCase("Classroom");

        slotRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                boolean found = false;
                for (MutableData slot : currentData.getChildren()) {
                    String start = slot.child("start").getValue(String.class);
                    if (start != null && start.replace(":", "").equals(slotStart)) {
                        String status = slot.child("status").getValue(String.class);
                        if ("AVAILABLE".equalsIgnoreCase(status)) {
                            // If classroom, mark as BOOKED immediately (direct booking)
                            // Otherwise, mark as Pending for approval
                            slot.child("status").setValue(isClassroom ? "BOOKED" : "Pending");
                            found = true;
                            break;
                        } else {
                            // Slot taken
                            return Transaction.abort();
                        }
                    }
                }
                return found ? Transaction.success(currentData) : Transaction.abort();
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    saveBookingData();
                } else {
                    buttonSubmit.setEnabled(true);
                    String msg = "Slot no longer available.";
                    if (error != null) {
                        Log.e(TAG, "Transaction failed: " + error.getMessage());
                        if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                            msg = "Permission Denied: Please check Firebase rules.";
                        } else {
                            msg = "Error: " + error.getMessage();
                        }
                    }
                    Toast.makeText(BookingFormActivity.this, msg, Toast.LENGTH_LONG).show();
                    if (!committed && error == null) {
                        finish();
                    }
                }
            }
        });
    }

    private void saveBookingData() {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        String bookingId = bookingsRef.push().getKey();

        if (bookingId == null || uid == null) return;

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date now = new Date();

        Map<String, String> reqTime = new HashMap<>();
        reqTime.put("date", sdfDate.format(now));
        reqTime.put("time", sdfTime.format(now));

        boolean isClassroom = spaceType != null && spaceType.equalsIgnoreCase("Classroom");

        Map<String, Object> data = new HashMap<>();
        data.put("bookingId", bookingId);
        data.put("bookedBy", uid);
        data.put("bookedTime", reqTime);
        data.put("purpose", etPurpose.getText() != null ? etPurpose.getText().toString().trim() : "");
        data.put("description", etDescription.getText() != null ? etDescription.getText().toString().trim() : "");
        data.put("lorUpload", etLorUrl.getText() != null ? etLorUrl.getText().toString().trim() : "");
        data.put("scheduleId", scheduleId);
        data.put("slotStart", slotStart);
        data.put("date", date);
        data.put("timeSlot", timeSlot);
        data.put("spaceName", spaceName);
        data.put("spaceId", spaceId);
        
        // Final Status Logic merging HEAD and venkat
        data.put("status", isClassroom ? "Approved" : "Pending");
        data.put("approvedBy", isClassroom ? "System (Auto)" : "");
        data.put("facultyInchargeApproval", isClassroom);
        data.put("hodApproval", isClassroom);
        if (isClassroom) {
            data.put("remarks", "Directly booked (First Come First Serve)");
        }

        bookingsRef.child(bookingId).setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String toastMsg = isClassroom ? "Booking Confirmed!" : "Booking Request Sent";
                Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
                
                // 1. Local Notification (HEAD feature)
                NotificationHelper.showNotification(
                    this, 
                    isClassroom ? "Booking Confirmed" : "Booking Submitted", 
                    isClassroom ? "Your booking for " + spaceName + " is confirmed!" : "Booking request submitted successfully"
                );

                // 2. Notify Staff Incharge (venkat feature)
                FirebaseRepository repo = new FirebaseRepository();
                if (spaceId != null) {
                    repo.notifyStaffInchargeForSpace(
                        spaceId, 
                        spaceName, 
                        (isClassroom ? "Auto-confirmed booking: " : "New booking request: ") + spaceName + " on " + date, 
                        bookingId, 
                        uid, 
                        "booking", 
                        "staff"
                    );
                }

                // Close and return
                Intent intent = new Intent(this, BookingUserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("ROLE", userRole);
                startActivity(intent);
                finish();
            } else {
                // Rollback logic
                FirebaseDatabase.getInstance().getReference("schedules").child(scheduleId).child("slots")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot slot : snapshot.getChildren()) {
                                    String start = slot.child("start").getValue(String.class);
                                    if (start != null && start.replace(":", "").equals(slotStart)) {
                                        slot.getRef().child("status").setValue("AVAILABLE");
                                        break;
                                    }
                                }
                            }
                            @Override public void onCancelled(@NonNull DatabaseError error) {}
                        });

                buttonSubmit.setEnabled(true);
                Toast.makeText(this, "Failed to save booking.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
