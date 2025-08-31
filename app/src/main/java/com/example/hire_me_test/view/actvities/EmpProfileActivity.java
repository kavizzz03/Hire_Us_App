package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hire_me_test.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmpProfileActivity extends AppCompatActivity {

    TextView textViewWelcome, textViewJobTitle, textViewId;
    Button btnFindJobs, btnMyVault, btnMyReviews, btnViewProfile,
            btnEditJobs, btnJobHistory, btnLogout, btnOrderFood, chatBtn;

    private String idNumber; // keep it global for reuse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_profile);

        // Get data from intent
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("full_name");
        String jobTitle = intent.getStringExtra("job_title");
        idNumber = intent.getStringExtra("id_number");

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
        btnOrderFood = findViewById(R.id.btnOrderFood);
        chatBtn = findViewById(R.id.chatBtn);

        // Display passed user info
        textViewWelcome.setText("👋 Welcome, " + fullName);
        textViewJobTitle.setText("🧑‍💼 Job Title: " + jobTitle);
        textViewId.setText("🆔 ID: " + idNumber);

        // Bottom Navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // already here -> do nothing
                return true;
            } else if (id == R.id.nav_salary) {
                openActivity(VaultActivity.class);
                return true;
            } else if (id == R.id.nav_notifications) {
                openActivity(ChatActivity.class);
                return true;
            } else if (id == R.id.nav_profile) {
                openActivity(ViewProfileActivity.class);
                return true;
            }
            return false;
        });

        // Handle button clicks
        btnFindJobs.setOnClickListener(v -> openActivity(JobListActivity.class));
        btnMyVault.setOnClickListener(v -> openActivity(VaultActivity.class));
        btnMyReviews.setOnClickListener(v -> openActivity(WorkerReviewActivity.class));
        btnViewProfile.setOnClickListener(v -> openActivity(ViewProfileActivity.class));
        btnEditJobs.setOnClickListener(v -> openActivity(WorkerApplicationsActivity.class));
        btnJobHistory.setOnClickListener(v -> openActivity(EmpJobHistoryActivity.class));
        btnOrderFood.setOnClickListener(v -> openActivity(OrderFoodActivity.class));
        chatBtn.setOnClickListener(v -> openActivity(ChatActivity.class));

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(EmpProfileActivity.this, FindJobActivity.class));
            finish();
        });
    }

    // helper method to avoid duplicate intent code
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(EmpProfileActivity.this, activityClass);
        intent.putExtra("id_number", idNumber);
        startActivity(intent);
    }
}
