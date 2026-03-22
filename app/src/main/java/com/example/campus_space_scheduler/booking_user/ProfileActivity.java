package com.example.campus_space_scheduler.booking_user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campus_space_scheduler.LoginActivity;
import com.example.campus_space_scheduler.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private TextView nameTextView, emailTextView, phoneTextView, roleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_activity_profile);

        // Initialize views
        nameTextView = findViewById(R.id.textViewNameValue);
        emailTextView = findViewById(R.id.textViewEmailValue);
        phoneTextView = findViewById(R.id.textViewPhoneNumberValue);
        roleTextView = findViewById(R.id.textViewRoleValue);

        MaterialButton logout = findViewById(R.id.buttonLogout);
        ImageView buttonBack = findViewById(R.id.buttonBack);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            fetchUserProfile(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (buttonBack != null) {
            buttonBack.setOnClickListener(v -> finish());
        }

        if (logout != null) {
            logout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                // Clear session time on logout
                getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().remove("last_active_time").apply();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Record the time when the user leaves the activity
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .edit()
                .putLong("last_active_time", System.currentTimeMillis())
                .apply();
    }

    private void fetchUserProfile(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("emailId").getValue(String.class);
                    if (email == null) email = snapshot.child("email").getValue(String.class);

                    String phone = snapshot.child("phonenumber").getValue(String.class);
                    if (phone == null) phone = snapshot.child("phoneNumber").getValue(String.class);
                    if (phone == null) phone = snapshot.child("phone").getValue(String.class);

                    String role = snapshot.child("role").getValue(String.class);

                    // Update UI
                    nameTextView.setText(name != null ? name : "N/A");
                    emailTextView.setText(email != null ? email : "N/A");
                    phoneTextView.setText(phone != null ? phone : "N/A");

                    if (roleTextView != null) {
                        roleTextView.setText(role != null ? role : "N/A");
                    }
                } else {
                    Log.d(TAG, "No user data found for UID: " + uid);
                    Toast.makeText(ProfileActivity.this, "Profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
