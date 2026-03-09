package com.example.labadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class HallDetailsActivity extends AppCompatActivity {

    private HallListActivity.HallInfo hall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_details);

        hall = (HallListActivity.HallInfo) getIntent().getSerializableExtra("hall");

        if (hall != null) {
            updateUI();
        }

        MaterialButton btnEditHall = findViewById(R.id.btnEditHall);
        btnEditHall.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditHallActivity.class);
            intent.putExtra("hall", hall);
            startActivityForResult(intent, 100);
        });
    }

    private void updateUI() {
        TextView txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText(hall.name);

        setupDetailRow(R.id.rowCapacity, "CAPACITY", hall.capacity);
        setupDetailRow(R.id.rowAddress, "ADDRESS", hall.address);
        setupDetailRow(R.id.rowChairs, "NO. OF CHAIRS", hall.chairs);
        setupDetailRow(R.id.rowComputers, "NO. OF COMPUTERS", hall.computers);
        setupDetailRow(R.id.rowTables, "NO. OF TABLES", hall.tables);
        setupDetailRow(R.id.rowPlugBoards, "NO. OF PLUG BOARDS", hall.plugBoards);
    }

    private void setupDetailRow(int viewId, String label, String value) {
        View row = findViewById(viewId);
        TextView txtLabel = row.findViewById(R.id.label);
        TextView txtValue = row.findViewById(R.id.value);
        txtLabel.setText(label);
        txtValue.setText(value);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            hall = (HallListActivity.HallInfo) data.getSerializableExtra("hall");
            updateUI();
        }
    }
}