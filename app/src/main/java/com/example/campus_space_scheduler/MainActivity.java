package com.example.campus_space_scheduler;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;



public class MainActivity extends AppCompatActivity {
    EditText input;
    TextView output;
    Button btn;

    DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        input = findViewById(R.id.inputText);
        output = findViewById(R.id.outputText);
        btn = findViewById(R.id.btnSend);

        dbRef = FirebaseDatabase.getInstance().getReference("message");

        btn.setOnClickListener(v -> {
            String text = input.getText().toString();
            dbRef.setValue(text);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                output.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                output.setText("Error");
            }
        });

    }
}