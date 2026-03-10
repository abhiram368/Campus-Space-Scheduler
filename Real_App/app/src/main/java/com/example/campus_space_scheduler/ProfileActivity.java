package com.example.campus_space_scheduler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private TextView nameTextView, emailTextView, phoneTextView, rollNumberTextView, roleTextView;
    private LinearLayout rollNumberSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        nameTextView = findViewById(R.id.textViewNameValue);
        emailTextView = findViewById(R.id.textViewEmailValue);
        phoneTextView = findViewById(R.id.textViewPhoneNumberValue);
        rollNumberTextView = findViewById(R.id.textViewRollNumberValue);
        roleTextView = findViewById(R.id.textViewRoleValue);
        rollNumberSection = findViewById(R.id.rollNumberSection);
        
        MaterialButton logout = findViewById(R.id.buttonLogout);
        ImageView buttonBack = findViewById(R.id.buttonBack);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            fetchUserProfile(currentUser.getEmail());
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
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void fetchUserProfile(String currentUserEmail) {
        if (currentUserEmail == null) return;

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        
        usersRef.orderByChild("emailId").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    
                    String name = userSnapshot.child("name").getValue(String.class);
                    
                    String email = userSnapshot.child("emailId").getValue(String.class);
                    if (email == null) email = userSnapshot.child("email").getValue(String.class);

                    String phone = userSnapshot.child("phonenumber").getValue(String.class);
                    if (phone == null) phone = userSnapshot.child("phoneNumber").getValue(String.class);
                    if (phone == null) phone = userSnapshot.child("phone").getValue(String.class);

                    String rollNumber = userSnapshot.child("rollnumber").getValue(String.class);
                    if (rollNumber == null) rollNumber = userSnapshot.child("rollNumber").getValue(String.class);

                    String role = userSnapshot.child("role").getValue(String.class);

                    // Update UI
                    nameTextView.setText(name != null ? name : "N/A");
                    emailTextView.setText(email != null ? email : "N/A");
                    phoneTextView.setText(phone != null ? phone : "N/A");
                    
                    if (roleTextView != null) {
                        roleTextView.setText(role != null ? role : "N/A");
                    }

                    // Show roll number only for students
                    if (role != null && role.equalsIgnoreCase("student")) {
                        rollNumberSection.setVisibility(View.VISIBLE);
                        rollNumberTextView.setText(rollNumber != null ? rollNumber : "N/A");
                    } else {
                        rollNumberSection.setVisibility(View.GONE);
                    }
                } else {
                    Log.d(TAG, "No user data found for: " + currentUserEmail);
                    Toast.makeText(ProfileActivity.this, "Profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
}
