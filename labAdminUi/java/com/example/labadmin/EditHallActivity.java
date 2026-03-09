package com.example.labadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class EditHallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hall);

        HallListActivity.HallInfo hall = (HallListActivity.HallInfo) getIntent().getSerializableExtra("hall");

        EditText etName = findViewById(R.id.etName);
        EditText etCapacity = findViewById(R.id.etCapacity);
        EditText etAddress = findViewById(R.id.etAddress);
        EditText etChairs = findViewById(R.id.etChairs);
        EditText etComputers = findViewById(R.id.etComputers);
        EditText etTables = findViewById(R.id.etTables);
        EditText etPlugBoards = findViewById(R.id.etPlugBoards);
        MaterialButton btnSave = findViewById(R.id.btnSaveChanges);

        if (hall != null) {
            etName.setText(hall.name);
            etCapacity.setText(hall.capacity);
            etAddress.setText(hall.address);
            etChairs.setText(hall.chairs);
            etComputers.setText(hall.computers);
            etTables.setText(hall.tables);
            etPlugBoards.setText(hall.plugBoards);
        }

        btnSave.setOnClickListener(v -> {
            HallListActivity.HallInfo updatedHall = new HallListActivity.HallInfo(
                    etName.getText().toString(),
                    etCapacity.getText().toString(),
                    etAddress.getText().toString(),
                    etChairs.getText().toString(),
                    etComputers.getText().toString(),
                    etTables.getText().toString(),
                    etPlugBoards.getText().toString()
            );

            Intent resultIntent = new Intent();
            resultIntent.putExtra("hall", updatedHall);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}