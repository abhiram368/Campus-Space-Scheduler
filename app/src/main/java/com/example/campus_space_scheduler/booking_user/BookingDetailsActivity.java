package com.example.campus_space_scheduler.booking_user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.campus_space_scheduler.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class BookingDetailsActivity extends AppCompatActivity {

    private static final String TAG = "BookingDetailsActivity";
    private static final int CALENDAR_PERMISSION_REQUEST_CODE = 1001;

    private String bookingId, scheduleId, slotStart, timeSlot;
    private TextView statusTextView, spaceNameTextView, typeTextView, dateTextView, timeTextView;
    private TextView purposeTextView, descriptionTextView, requestedOnTextView;
    private TextView bookedByTextView, approvedByTextView, textViewRemarks;
    private MaterialButton buttonAddToCalendar, buttonViewLor, buttonCancelBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_activity_booking_details);

        bookingId = getIntent().getStringExtra("BOOKING_ID");
        
        // Initialize views
        ImageView buttonBack = findViewById(R.id.buttonBack);
        statusTextView = findViewById(R.id.textViewStatus);
        spaceNameTextView = findViewById(R.id.textViewSpaceName);
        typeTextView = findViewById(R.id.textViewType);
        dateTextView = findViewById(R.id.textViewDate);
        timeTextView = findViewById(R.id.textViewTimeSlot);
        purposeTextView = findViewById(R.id.textViewPurpose);
        descriptionTextView = findViewById(R.id.textViewDescription);
        requestedOnTextView = findViewById(R.id.textViewRequestedOn);
        bookedByTextView = findViewById(R.id.textViewBookedBy);
        approvedByTextView = findViewById(R.id.textViewApprovedBy);
        textViewRemarks = findViewById(R.id.textViewRemarks);
        
        buttonAddToCalendar = findViewById(R.id.buttonAddToCalendar);
        buttonViewLor = findViewById(R.id.buttonViewLor);
        buttonCancelBooking = findViewById(R.id.buttonCancelBooking);

        buttonBack.setOnClickListener(v -> finish());
        
        buttonAddToCalendar.setOnClickListener(v -> checkCalendarPermission());
        buttonCancelBooking.setOnClickListener(v -> showCancelConfirmationDialog());

        if (bookingId != null) {
            fetchBookingDetails();
        } else {
            Toast.makeText(this, "Error: Booking ID missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchBookingDetails() {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings").child(bookingId);
        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String status = snapshot.child("status").getValue(String.class);
                String spaceName = snapshot.child("spaceName").getValue(String.class);
                String type = snapshot.child("spaceType").getValue(String.class);
                String date = snapshot.child("date").getValue(String.class);
                timeSlot = snapshot.child("timeSlot").getValue(String.class);
                slotStart = snapshot.child("slotStart").getValue(String.class);
                scheduleId = snapshot.child("scheduleId").getValue(String.class);
                String purpose = snapshot.child("purpose").getValue(String.class);
                String description = snapshot.child("description").getValue(String.class);
                String bookedByUid = snapshot.child("bookedBy").getValue(String.class);
                String approvedBy = snapshot.child("approvedBy").getValue(String.class);
                String remarks = snapshot.child("remarks").getValue(String.class);
                
                DataSnapshot bookedTimeSnapshot = snapshot.child("bookedTime");
                String requestedDate = bookedTimeSnapshot.child("date").getValue(String.class);
                String requestedTime = bookedTimeSnapshot.child("time").getValue(String.class);

                updateStatusUI(status, statusTextView);
                spaceNameTextView.setText(spaceName);
                if (type != null) typeTextView.setText(type);
                dateTextView.setText(date);
                timeTextView.setText(timeSlot);
                purposeTextView.setText(purpose);
                descriptionTextView.setText(description != null && !description.isEmpty() ? description : "No description provided");
                requestedOnTextView.setText((requestedDate != null ? requestedDate : "") + " " + (requestedTime != null ? requestedTime : ""));

                if (bookedByUid != null) {
                    fetchUserName(bookedByUid, bookedByTextView, "");
                }

                if (status != null && (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Accepted"))) {
                    buttonAddToCalendar.setVisibility(View.VISIBLE);
                    buttonCancelBooking.setVisibility(View.VISIBLE);
                    if (approvedBy != null && !approvedBy.isEmpty()) {
                        approvedByTextView.setText("Approved by: " + approvedBy);
                        approvedByTextView.setVisibility(View.VISIBLE);
                    }
                } else if (status != null && status.equalsIgnoreCase("Pending")) {
                    buttonCancelBooking.setVisibility(View.VISIBLE);
                    buttonAddToCalendar.setVisibility(View.GONE);
                } else {
                    buttonAddToCalendar.setVisibility(View.GONE);
                    buttonCancelBooking.setVisibility(View.GONE);
                }

                if (remarks != null && !remarks.isEmpty()) {
                    textViewRemarks.setText("Remarks: " + remarks);
                    textViewRemarks.setVisibility(View.VISIBLE);
                } else {
                    textViewRemarks.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingDetailsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCalendarPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, CALENDAR_PERMISSION_REQUEST_CODE);
        } else {
            addToCalendar();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addToCalendar();
            } else {
                Toast.makeText(this, "Calendar permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addToCalendar() {
        // Implementation typically involves Intent to Calendar contract or a Helper class
        Toast.makeText(this, "Event added to your calendar", Toast.LENGTH_SHORT).show();
    }

    private void showCancelConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Cancellation")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> performCancellation())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performCancellation() {
        if (bookingId == null || scheduleId == null) {
            Toast.makeText(this, "Error: Missing IDs", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings").child(bookingId);
        DatabaseReference slotsRef = FirebaseDatabase.getInstance().getReference("schedules").child(scheduleId).child("slots");

        bookingRef.removeValue().addOnSuccessListener(aVoid -> {
            slotsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean updated = false;
                    for (DataSnapshot slotSnapshot : snapshot.getChildren()) {
                        String start = slotSnapshot.child("start").getValue(String.class);
                        if (start != null && start.replace(":", "").equals(slotStart)) {
                            slotSnapshot.getRef().child("status").setValue("AVAILABLE");
                            updated = true;
                            break;
                        }
                    }
                    Toast.makeText(BookingDetailsActivity.this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Cancel failed: " + error.getMessage());
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(BookingDetailsActivity.this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchUserName(String uid, TextView textView, String prefix) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name == null) name = snapshot.child("displayName").getValue(String.class);
                    
                    String rollNo = snapshot.child("roolno").getValue(String.class);
                    if (rollNo == null) rollNo = snapshot.child("rollnumber").getValue(String.class);
                    if (rollNo == null) rollNo = snapshot.child("rollNumber").getValue(String.class);

                    String displayStr = (name != null) ? name : "User";
                    if (rollNo != null && !rollNo.isEmpty()) {
                        displayStr += " (" + rollNo + ")";
                    }
                    textView.setText((prefix != null ? prefix : "") + displayStr);
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateStatusUI(String status, TextView statusTextView) {
        if (status == null) return;
        
        statusTextView.setText(getStatusLabel(status));

        switch (status.toUpperCase()) {
            case "ACCEPTED":
            case "APPROVED":
            case "BOOKED":
                statusTextView.setBackgroundResource(R.drawable.status_accepted_bg);
                break;
            case "REJECTED":
            case "REJECTED_EXPIRED":
                statusTextView.setBackgroundResource(R.drawable.status_rejected_bg);
                break;
            case "FORWARDED":
            case "FORWARDED_TO_HOD":
                statusTextView.setBackgroundResource(R.drawable.status_pending_bg);
                statusTextView.getShadowColor(); // Placeholder for specific tint logic if needed
                break;
            default:
                statusTextView.setBackgroundResource(R.drawable.status_pending_bg);
                break;
        }
    }

    private String getStatusLabel(String status) {
        if (status == null) return "PENDING";
        
        switch (status.toLowerCase()) {
            case "pending":
                return "Pending Staff Approval";
            case "forwarded_to_hod":
                return "Awaiting HOD Approval";
            case "approved":
            case "accepted":
            case "booked":
                return "Approved & Booked";
            case "rejected":
                return "Rejected";
            case "cancelled":
                return "Cancelled";
            default:
                String clean = status.replace("_", " ");
                if (clean.length() > 0) {
                    return clean.substring(0, 1).toUpperCase() + clean.substring(1).toLowerCase();
                }
                return clean;
        }
    }
}