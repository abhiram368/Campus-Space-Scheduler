package com.example.campus_space_scheduler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminManagementActivity extends AppCompatActivity {

    private String mode; // "USER" or "SPACE"
    private DatabaseReference dbRef;
    private RecyclerView recyclerView;
    private MaterialToolbar toolbar;
    private ExtendedFloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_management);

        // 1. Get Mode from Intent
        mode = getIntent().getStringExtra("MANAGEMENT_MODE");
        if (mode == null) mode = "USER"; // Default fallback

        // 2. Init Views
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        fabAdd = findViewById(R.id.fab_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Adapt UI based on Mode
        setupMode();
    }

    private void setupMode() {
        if (mode.equals("USER")) {
            toolbar.setTitle("User Management");
            fabAdd.setText("Add New User");
            dbRef = FirebaseDatabase.getInstance().getReference("users");
        } else {
            toolbar.setTitle("Space Management");
            fabAdd.setText("Add New Space");
            dbRef = FirebaseDatabase.getInstance().getReference("spaces");
        }

        fetchDataFromFirebase();

        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void fetchDataFromFirebase() {
        // Here you would set up your Firebase RecyclerAdapter
        // which automatically syncs your list with the DB.
    }

    private void showAddDialog() {
        // Show a MaterialAlertDialog to input data
        // If USER mode: show Name/Email fields
        // If SPACE mode: show Room/Capacity fields
    }
}
