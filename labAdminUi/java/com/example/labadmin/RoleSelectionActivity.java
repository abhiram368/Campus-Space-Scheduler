package com.example.labadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("RoleSelection", "onCreate started");
        try {
            setContentView(R.layout.activity_role_selection);
            Log.d("RoleSelection", "setContentView finished");

            CardView cardLabAdmin = findViewById(R.id.cardLabAdmin);
            CardView cardHallIncharge = findViewById(R.id.cardHallIncharge);

            if (cardLabAdmin != null) {
                cardLabAdmin.setOnClickListener(v -> {
                    Intent intent = new Intent(RoleSelectionActivity.this, LabAdminDashboardActivity.class);
                    startActivity(intent);
                });
            } else {
                Log.e("RoleSelection", "cardLabAdmin is null");
            }

            if (cardHallIncharge != null) {
                cardHallIncharge.setOnClickListener(v -> {
                    Intent intent = new Intent(RoleSelectionActivity.this, HallInchargeDashboardActivity.class);
                    startActivity(intent);
                });
            } else {
                Log.e("RoleSelection", "cardHallIncharge is null");
            }
        } catch (Exception e) {
            Log.e("RoleSelection", "Error in onCreate", e);
        }
    }
}