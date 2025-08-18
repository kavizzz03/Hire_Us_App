package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hire_me_test.R;

public class DashboardActivity extends AppCompatActivity {

    TextView welcomeText;
    Button makeJobBtn, jobHistoryBtn, logoutBtn, withdrawRequestBtn, editJobBtn, chatBtn;

    String userId, companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        makeJobBtn = findViewById(R.id.makeJobBtn);
        jobHistoryBtn = findViewById(R.id.jobHistoryBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        withdrawRequestBtn = findViewById(R.id.withdrawRequestBtn);
        editJobBtn = findViewById(R.id.editJobBtn);
        chatBtn = findViewById(R.id.chatBtn);

        // Get user data from Intent
        userId = getIntent().getStringExtra("user_id");
        companyName = getIntent().getStringExtra("company_name");

        if (companyName != null && !companyName.isEmpty()) {
            welcomeText.setText("Welcome, " + companyName);
        } else {
            welcomeText.setText("Welcome, Employer");
        }

        // Button actions
        makeJobBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, PostJobActivity.class);
            intent.putExtra("employer_id", userId);
            startActivity(intent);
        });

        jobHistoryBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, EmployerJobsHistoryActivity.class);
            intent.putExtra("employer_id", userId);
            startActivity(intent);
        });

        withdrawRequestBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, WithdrawRequestActivity.class);
            intent.putExtra("employer_id", userId);
            startActivity(intent);
        });

        editJobBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, EmployerJobsActivity.class);
            intent.putExtra("employer_id", userId);
            startActivity(intent);
        });

        chatBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(view -> {
            // Go back to login (change GiveJobActivity to your actual login activity if needed)
            Intent intent = new Intent(DashboardActivity.this, GiveJobActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
