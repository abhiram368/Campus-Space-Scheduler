package com.example.labadmin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HallInchargeDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_incharge_dashboard);

        CardView cardPendingRequests = findViewById(R.id.cardPendingRequests);
        CardView cardLiveStatus = findViewById(R.id.cardLiveStatus);
        CardView cardViewSchedule = findViewById(R.id.cardViewSchedule);
        CardView cardApprovalHistory = findViewById(R.id.cardApprovalHistory);
        CardView cardHallDetails = findViewById(R.id.cardHallDetails);

        cardPendingRequests.setOnClickListener(v -> {
            Intent intent = new Intent(this, PendingRequestsActivity.class);
            startActivity(intent);
        });
        
        cardLiveStatus.setOnClickListener(v -> {
            Intent intent = new Intent(this, LiveStatusActivity.class);
            startActivity(intent);
        });
            
        cardViewSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewScheduleActivity.class);
            startActivity(intent);
        });
            
        cardApprovalHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, ApprovalHistoryActivity.class);
            startActivity(intent);
        });
            
        cardHallDetails.setOnClickListener(v -> {
            Intent intent = new Intent(this, HallListActivity.class);
            startActivity(intent);
        });
    }
}