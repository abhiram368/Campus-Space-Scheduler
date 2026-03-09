package com.example.campus_space_scheduler;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class BookingFormActivity extends AppCompatActivity {

    private String spaceName;
    private String date;
    private String timeSlot;
    private String userRole;
    private String spaceType;
    private String scheduleId;

    private TextInputEditText etPurpose, etLorUrl, etDescription;
    private TextInputLayout textInputLayoutLorUrl;
    private MaterialButton buttonDownloadLorFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        // Get data from intent
        spaceName = getIntent().getStringExtra("SPACE_NAME");
        date = getIntent().getStringExtra("DATE");
        timeSlot = getIntent().getStringExtra("TIME_SLOT");
        userRole = getIntent().getStringExtra("ROLE");
        spaceType = getIntent().getStringExtra("SPACE_TYPE");
        scheduleId = getIntent().getStringExtra("SCHEDULE_ID");

        // Initialize views
        ImageView buttonBack = findViewById(R.id.buttonBack);
        TextView textViewSpaceName = findViewById(R.id.textViewSpaceName);
        TextView textViewSelectedSlot = findViewById(R.id.textViewSelectedSlot);
        
        etPurpose = findViewById(R.id.etPurpose);
        etLorUrl = findViewById(R.id.etLorUrl);
        etDescription = findViewById(R.id.etDescription);
        textInputLayoutLorUrl = findViewById(R.id.textInputLayoutLorUrl);
        buttonDownloadLorFormat = findViewById(R.id.buttonDownloadLorFormat);
        
        MaterialButton buttonSubmit = findViewById(R.id.btnSubmit);

        // Set UI text
        if (spaceName != null) {
            textViewSpaceName.setText(spaceName);
        }
        if (date != null && timeSlot != null) {
            textViewSelectedSlot.setText(date + " | " + timeSlot);
        }

        updateUIBasedOnRole(userRole, spaceType);

        buttonBack.setOnClickListener(v -> finish());

        buttonSubmit.setOnClickListener(v -> {
            if (validateForm(userRole, spaceType)) {
                submitBooking();
            }
        });
    }

    private void updateUIBasedOnRole(String role, String spaceType) {
        if (role == null || spaceType == null) return;

        if (role.equalsIgnoreCase("Student")) {
            if (spaceType.equalsIgnoreCase("Classroom")) {
                textInputLayoutLorUrl.setVisibility(View.GONE);
                buttonDownloadLorFormat.setVisibility(View.GONE);
            } else if (spaceType.equalsIgnoreCase("Lab") || spaceType.equalsIgnoreCase("Hall")) {
                textInputLayoutLorUrl.setVisibility(View.VISIBLE);
                buttonDownloadLorFormat.setVisibility(View.VISIBLE);
                textInputLayoutLorUrl.setHint("LOR URL (Mandatory for Students) *");
            }
        } else if (role.equalsIgnoreCase("Faculty")) {
            if (spaceType.equalsIgnoreCase("Lab") || spaceType.equalsIgnoreCase("Hall")) {
                textInputLayoutLorUrl.setVisibility(View.VISIBLE);
                buttonDownloadLorFormat.setVisibility(View.VISIBLE);
                textInputLayoutLorUrl.setHint("LOR URL (Optional for Faculty)");
            } else {
                textInputLayoutLorUrl.setVisibility(View.GONE);
                buttonDownloadLorFormat.setVisibility(View.GONE);
            }
        }
    }

    private boolean validateForm(String role, String spaceType) {
        String purpose = etPurpose.getText().toString().trim();
        if (TextUtils.isEmpty(purpose)) {
            etPurpose.setError("Purpose is required");
            return false;
        }

        // Conditional Mandatory Check for Students
        if (role != null && role.equalsIgnoreCase("Student") && 
            (spaceType != null && (spaceType.equalsIgnoreCase("Lab") || spaceType.equalsIgnoreCase("Hall")))) {
            if (TextUtils.isEmpty(etLorUrl.getText().toString().trim())) {
                etLorUrl.setError("LOR URL is required for Lab/Hall bookings");
                return false;
            }
        }
        return true;
    }

    private void submitBooking() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String purpose = etPurpose.getText().toString().trim();
        String lorUrl = etLorUrl.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Reference the "bookings" node
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        String bookingId = bookingsRef.push().getKey();

        // Prepare the map
        HashMap<String, Object> booking = new HashMap<>();
        booking.put("bookedBy", "users/" + uid);
        booking.put("description", description);
        booking.put("lorUrl", lorUrl);
        booking.put("purpose", purpose);
        booking.put("scheduleId", "schedules/" + scheduleId);
        booking.put("status", "pending");
        booking.put("AcceptedBy", ""); 

        // Upload to Firebase
        if (bookingId != null) {
            bookingsRef.child(bookingId).setValue(booking)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking Submitted!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to previous screen
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
