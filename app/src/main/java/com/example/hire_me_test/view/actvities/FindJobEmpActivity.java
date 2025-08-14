package com.example.hire_me_test.view.actvities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;

import java.util.HashMap;
import java.util.Map;

public class FindJobEmpActivity extends AppCompatActivity {

    TextView tvJobTitle, tvCompanyName, tvVacancies, tvTimeRange, tvLocation,
            tvBasicSalary, tvOtSalary, tvRequirements, tvJobDate, tvPickupLocation,
            tvContactInfo, tvEmail;

    Button btnApply;

    private int jobId;
    private String idNumber;

    private static final String APPLY_JOB_URL = "https://hireme.cpsharetxt.com/apply_job.php";
    private static final String CHANNEL_ID = "job_application_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job_emp);

        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvVacancies = findViewById(R.id.tvVacancies);
        tvTimeRange = findViewById(R.id.tvTimeRange);
        tvLocation = findViewById(R.id.tvLocation);
        tvBasicSalary = findViewById(R.id.tvBasicSalary);
        tvOtSalary = findViewById(R.id.tvOtSalary);
        tvRequirements = findViewById(R.id.tvRequirements);
        tvJobDate = findViewById(R.id.tvJobDate);
        tvPickupLocation = findViewById(R.id.tvPickupLocation);
        tvContactInfo = findViewById(R.id.tvContactInfo);
        tvEmail = findViewById(R.id.tvEmail);

        btnApply = findViewById(R.id.btnApply);

        // Get job details from intent
        jobId = getIntent().getIntExtra("job_id", -1);
        idNumber = getIntent().getStringExtra("id_number");

        tvJobTitle.setText(getIntent().getStringExtra("job_title"));
        tvCompanyName.setText(getIntent().getStringExtra("company_name"));
        tvVacancies.setText("Vacancies: " + getIntent().getStringExtra("vacancies"));
        tvTimeRange.setText("Time: " + getIntent().getStringExtra("time_range"));
        tvLocation.setText("Location: " + getIntent().getStringExtra("location"));
        tvBasicSalary.setText("Basic Salary: Rs. " + getIntent().getStringExtra("basic_salary"));
        tvOtSalary.setText("OT Salary: Rs. " + getIntent().getStringExtra("ot_salary"));
        tvRequirements.setText("Requirements: " + getIntent().getStringExtra("requirements"));
        tvJobDate.setText("Date: " + getIntent().getStringExtra("job_date"));
        tvPickupLocation.setText("Pickup Location: " + getIntent().getStringExtra("pickup_location"));
        tvContactInfo.setText("Contact: " + getIntent().getStringExtra("contact_info"));
        tvEmail.setText("Email: " + getIntent().getStringExtra("email"));

        btnApply.setOnClickListener(v -> applyForJob());
    }

    private void applyForJob() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APPLY_JOB_URL,
                response -> {
                    Toast.makeText(FindJobEmpActivity.this, "Application Successful!", Toast.LENGTH_LONG).show();

                    // Show notification
                    showNotification("Job Application", "You have successfully applied for the job.");
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(FindJobEmpActivity.this, "Application failed", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("job_id", String.valueOf(jobId));
                params.put("id_number", idNumber);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create NotificationChannel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Job Application Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Intent to open this activity when user taps notification
        Intent intent = new Intent(this, FindJobEmpActivity.class);
        intent.putExtra("job_id", jobId);
        intent.putExtra("id_number", idNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)  // Using default app launcher icon here
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
