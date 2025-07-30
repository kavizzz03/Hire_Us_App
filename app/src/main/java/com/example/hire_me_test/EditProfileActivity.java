package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EmpProfileActivity extends AppCompatActivity {

    TextView textViewWelcome, textViewJobTitle, textViewId;
    Button btnFindJobs, btnMyVault, btnMyReviews, btnViewProfile, btnEditJobs, btnJobHistory, btnLogout, btnOrderFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_profile);

        // Get data from intent
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("full_name");
        String jobTitle = intent.getStringExtra("job_title");
        String idNumber = intent.getStringExtra("id_number");

        // Bind views
        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewJobTitle = findViewById(R.id.textViewJobTitle);
        textViewId = findViewById(R.id.textViewIdNumber);

        btnFindJobs = findViewById(R.id.btnFindJobs);
        btnMyVault = findViewById(R.id.btnMyVault);
        btnMyReviews = findViewById(R.id.btnMyReviews);
        btnViewProfile = findViewById(R.id.btnViewProfile);
        btnEditJobs = findViewById(R.id.btnEditJobs);
        btnJobHistory = findViewById(R.id.btnJobHistory);
        btnLogout = findViewById(R.id.btnLogout);
        btnOrderFood = findViewById(R.id.btnOrderFood); // New button

        // Display passed user info
        textViewWelcome.setText("👋 Welcome, " + fullName);
        textViewJobTitle.setText("🧑‍💼 Job Title: " + jobTitle);
        textViewId.setText("🆔 ID: " + idNumber);

        // Handle button clicks
        btnFindJobs.setOnClickListener(v -> {
            Intent intentFindJobs = new Intent(EmpProfileActivity.this, JobListActivity.class);
            intentFindJobs.putExtra("id_number", idNumber);
            startActivity(intentFindJobs);
        });

        btnMyVault.setOnClickListener(v -> {
            // Go to My Vault screen
        });

        btnMyReviews.setOnClickListener(v -> {
            Intent intentReview = new Intent(EmpProfileActivity.this, WorkerReviewActivity.class);
            intentReview.putExtra("id_number", idNumber);
            startActivity(intentReview);
        });

        btnViewProfile.setOnClickListener(v -> {
            Intent intentView = new Intent(EmpProfileActivity.this, ViewProfileActivity.class);
            intentView.putExtra("id_number", idNumber);
            startActivity(intentView);
        });

        btnEditJobs.setOnClickListener(v -> {
            // Go to Edit Jobs screen
        });

        btnJobHistory.setOnClickListener(v -> {
            // Go to Job History screen
        });

        btnOrderFood.setOnClickListener(v -> {
            // Navigate to order food screen
            Intent intentOrder = new Intent(EmpProfileActivity.this, OrderFoodActivity.class);
            intentOrder.putExtra("id_number", idNumber); // Optional
            startActivity(intentOrder);
        });

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(EmpProfileActivity.this, FindJobActivity.class));
            finish();
        });
    }
}
