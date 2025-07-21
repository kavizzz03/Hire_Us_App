package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    TextView textViewWelcome;
    ImageView imageViewUserIcon;
    Button buttonViewUpdateProfile, buttonMakeNewJob, buttonCurrentJobManagement, buttonJobHistory, buttonFeedback, buttonWithdrawRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        textViewWelcome = findViewById(R.id.textViewWelcome);
        imageViewUserIcon = findViewById(R.id.imageViewUserIcon);
        buttonViewUpdateProfile = findViewById(R.id.buttonViewUpdateProfile);
        buttonMakeNewJob = findViewById(R.id.buttonMakeNewJob);
        buttonCurrentJobManagement = findViewById(R.id.buttonCurrentJobManagement);
        buttonJobHistory = findViewById(R.id.buttonJobHistory);
        buttonFeedback = findViewById(R.id.buttonFeedback);
        buttonWithdrawRequests = findViewById(R.id.buttonWithdrawRequests);

        // For demo: get username and icon URL from Intent extras (you can replace with SharedPreferences or API later)
        String username = getIntent().getStringExtra("username");
        String iconUrl = getIntent().getStringExtra("iconUrl");

        if (username == null || username.isEmpty()) {
            username = "Employer";  // fallback
        }

        textViewWelcome.setText("Welcome, " + username);

        // For now, just use a placeholder icon (you can load image from URL with Glide/Picasso)
        //imageViewUserIcon.setImageResource(R.drawable.ic_user_placeholder);

        // Button click handlers
       /* buttonViewUpdateProfile.setOnClickListener(v -> {
            // Navigate to profile activity
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        buttonMakeNewJob.setOnClickListener(v -> {
            // Navigate to create new job activity
            Intent intent = new Intent(DashboardActivity.this, MakeJobActivity.class);
            startActivity(intent);
        });

        buttonCurrentJobManagement.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CurrentJobsActivity.class);
            startActivity(intent);
        });

        buttonJobHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, JobHistoryActivity.class);
            startActivity(intent);
        });

        buttonFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        buttonWithdrawRequests.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, WithdrawRequestsActivity.class);
            startActivity(intent);
        });*/
    }
}
