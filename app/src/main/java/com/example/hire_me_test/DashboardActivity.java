package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    TextView welcomeText;
    Button makeJobBtn;

    String userId, companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeText = findViewById(R.id.welcomeText);
        makeJobBtn = findViewById(R.id.makeJobBtn);

        userId = getIntent().getStringExtra("user_id");
        companyName = getIntent().getStringExtra("company_name");

        welcomeText.setText("Welcome, " + companyName);

        makeJobBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, PostJobActivity.class);
            intent.putExtra("employer_id", userId);
            startActivity(intent);
        });
    }
}
