package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    TextView welcomeText;
    Button makeJobBtn,jobHistoryBtn,logoutBtn;

    String userId, companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeText = findViewById(R.id.welcomeText);
        makeJobBtn = findViewById(R.id.makeJobBtn);
        jobHistoryBtn=findViewById(R.id.jobHistoryBtn);
        logoutBtn=findViewById(R.id.logoutBtn);

        userId = getIntent().getStringExtra("user_id");
        companyName = getIntent().getStringExtra("company_name");

        welcomeText.setText("Welcome, " + companyName);

        makeJobBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, PostJobActivity.class);
            intent.putExtra("employer_id", userId);
            startActivity(intent);
        });
        jobHistoryBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, EmployerJobsActivity.class);
            intent.putExtra("employer_id", userId);
            startActivity(intent);
        });
        logoutBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, GiveJobActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
