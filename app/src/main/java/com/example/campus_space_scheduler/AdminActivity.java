package com.example.campus_space_scheduler;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.campus_space_scheduler.databinding.ActivityAdminBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.OnBackPressedCallback;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin);

        // Initiate Binding
        ActivityAdminBinding binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize
        drawerLayout = binding.drawerLayout;
        // setup navigation view listener
        binding.navView.setNavigationItemSelectedListener(this);

        // Accessing the included views directly (no findViewById needed!)
        binding.cardUsers.statLabel.setText("Active Users");
        binding.cardUsers.statValue.setText("1,284");

        binding.cardSpaces.statLabel.setText("Total Spaces");
        binding.cardSpaces.statValue.setText("45");

        setupAdminButtons(binding);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    onBackPressed(); // Call original or finish()
                }
            }
        });
    }

    private void setupAdminButtons(ActivityAdminBinding binding) {
        binding.btnManageUsers.setOnClickListener(v -> {
            // Open the User Management Activity
            Intent intent = new Intent(this, AdminManagementActivity.class);
            intent.putExtra("MANAGEMENT_MODE", "USER");
            startActivity(intent);
        });
        binding.btnManageSpaces.setOnClickListener(v -> {
            // Open the Space Management Activity
            Intent intent = new Intent(this, AdminManagementActivity.class);
            intent.putExtra("MANAGEMENT_MODE", "SPACE");
            startActivity(intent);
        });
        binding.btnManageSchedule.setOnClickListener(v -> {
            // Open the Schedule Management Activity
            Intent intent = new Intent(this, AdminScheduleManagementActivity.class);
            startActivity(intent);
        });
        binding.btnViewLogs.setOnClickListener(v -> {
            // Open the Logs Activity
            Intent intent = new Intent(this, ViewLogsActivity.class);
            startActivity(intent);
        });
    }

    private void initiateScheduleProcess() {
        // Example: Triggering a Firebase Cloud Function or updating a "status" flag in Firestore
        Toast.makeText(this, "Schedule Task Started!", Toast.LENGTH_LONG).show();

        /* FirebaseDatabase.getInstance().getReference("system_commands")
            .child("trigger_schedule")
            .setValue(true);
        */
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Stay on dashboard
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
