package com.example.hire_me_test.view.actvities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import com.example.hire_me_test.view.adaptors.EmployerJobsAdapter;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.JobModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployerJobsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployerJobsAdapter adapter;
    private final ArrayList<JobModel> jobList = new ArrayList<>();
    private String employerId;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String CHANNEL_ID = "job_finish_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_jobs);

        employerId = getIntent().getStringExtra("employer_id");
        if (employerId == null) {
            Toast.makeText(this, "Employer ID missing!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerJobs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EmployerJobsAdapter(
                this,
                jobList,
                job -> {
                    Intent intent = new Intent(this, ApplicantsActivity.class);
                    intent.putExtra("job_id", job.getId());
                    startActivity(intent);
                },
                job -> {
                    Intent intent = new Intent(this, AddMealActivity.class);
                    intent.putExtra("job_id", job.getId());
                    startActivity(intent);
                },
                job -> showCommonRatingInputDialog(job.getId())
        );

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchJobs();
            swipeRefreshLayout.setRefreshing(false);
        });

        fetchJobs();
        createNotificationChannel();
    }

    // Fetch employer jobs
    private void fetchJobs() {
        String url = "https://hireme.cpsharetxt.com/get_employer_jobs.php?employer_id=" + employerId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("FetchJobsResponse", response);
                    try {
                        JSONArray array = new JSONArray(response);
                        jobList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            JobModel job = new JobModel(
                                    obj.getInt("id"),
                                    obj.getString("jobTitle"),
                                    obj.getString("companyName"),
                                    obj.getString("vacancies"),
                                    obj.getString("timeRange"),
                                    obj.getString("location"),
                                    obj.getString("basicSalary"),
                                    obj.getString("otSalary"),
                                    obj.getString("requirements"),
                                    obj.getString("jobDate"),
                                    obj.getString("pickupLocation"),
                                    obj.getString("contactInfo"),
                                    obj.getString("email")
                            );
                            jobList.add(job);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("JSONParseError", e.toString());
                        Toast.makeText(this, "Error parsing jobs", Toast.LENGTH_LONG).show();
                    }
                },
                error -> logVolleyError(error, "Error loading jobs"));

        Volley.newRequestQueue(this).add(request);
    }

    // Dialog for rating, feedback, experience
    private void showCommonRatingInputDialog(int jobId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Rate All Workers");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) getResources().getDimension(R.dimen.dialog_padding);
        layout.setPadding(padding, padding, padding, padding);

        // Rating input as number 1-5
        TextInputLayout ratingLayout = new TextInputLayout(this);
        ratingLayout.setHint("Enter rating (1-5)");
        TextInputEditText inputRating = new TextInputEditText(ratingLayout.getContext());
        inputRating.setInputType(InputType.TYPE_CLASS_NUMBER);
        ratingLayout.addView(inputRating);
        layout.addView(ratingLayout);

        // Feedback input
        TextInputLayout feedbackLayout = new TextInputLayout(this);
        feedbackLayout.setHint("Enter feedback");
        TextInputEditText inputFeedback = new TextInputEditText(feedbackLayout.getContext());
        feedbackLayout.addView(inputFeedback);
        layout.addView(feedbackLayout);

        // Experience input
        TextInputLayout expLayout = new TextInputLayout(this);
        expLayout.setHint("Enter work experience");
        TextInputEditText inputExperience = new TextInputEditText(expLayout.getContext());
        expLayout.addView(inputExperience);
        layout.addView(expLayout);

        builder.setView(layout);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String ratingStr = inputRating.getText().toString().trim();
            String feedback = inputFeedback.getText().toString().trim();
            String experience = inputExperience.getText().toString().trim();

            if (ratingStr.isEmpty() || feedback.isEmpty() || experience.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int rating = Integer.parseInt(ratingStr);
                if (rating < 1 || rating > 5) {
                    Toast.makeText(this, "Rating must be 1 to 5", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateRatingsForAll(jobId, rating, feedback, experience);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid rating format", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Send rating to server
    private void updateRatingsForAll(int jobId, int rating, String feedback, String experience) {
        String url = "https://hireme.cpsharetxt.com/update_worker_ratings.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("UpdateRatingsResponse", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("status").equalsIgnoreCase("success")) {
                            askForOtHoursAndFinishJob(jobId);
                        } else {
                            Toast.makeText(this, "Failed: " + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("UpdateRatingsParseError", e.toString());
                        Toast.makeText(this, "Error updating ratings", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> logVolleyError(error, "Error updating ratings")) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("job_id", String.valueOf(jobId));
                params.put("rating", String.valueOf(rating));
                params.put("feedback", feedback);
                params.put("work_experience", experience);
                params.put("rated_by", "Employer");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // OT hours prompt
    private void askForOtHoursAndFinishJob(int jobId) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Overtime Hours")
                .setMessage("Does this job have OT hours?")
                .setPositiveButton("Yes", (dialog, which) -> showInputOtHoursDialog(jobId))
                .setNegativeButton("No", (dialog, which) -> finishJob(jobId, 0))
                .show();
    }

    private void showInputOtHoursDialog(int jobId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Enter OT Hours");

        TextInputLayout otLayout = new TextInputLayout(this);
        otLayout.setHint("e.g., 2.5 hours");
        TextInputEditText input = new TextInputEditText(otLayout.getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        otLayout.addView(input);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) getResources().getDimension(R.dimen.dialog_padding);
        layout.setPadding(padding, padding, padding, padding);
        layout.addView(otLayout);
        builder.setView(layout);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String otHoursStr = input.getText().toString().trim();
            if (otHoursStr.isEmpty()) {
                Toast.makeText(this, "Please enter OT hours", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double otHours = Double.parseDouble(otHoursStr);
                if (otHours < 0) {
                    Toast.makeText(this, "OT hours cannot be negative", Toast.LENGTH_SHORT).show();
                    return;
                }
                finishJob(jobId, otHours);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid OT hours format", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Finish job
    private void finishJob(int jobId, double otHours) {
        String url = "https://hireme.cpsharetxt.com/finish_job.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("FinishJobResponse", response);
                    if (response.trim().equalsIgnoreCase("success")) {
                        Toast.makeText(this, "Job finished and vault updated!", Toast.LENGTH_SHORT).show();
                        fetchJobs();
                        showJobFinishedNotification();
                    } else {
                        Toast.makeText(this, "Failed: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> logVolleyError(error, "Finishing job failed")) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("job_id", String.valueOf(jobId));
                params.put("ot_hours", String.valueOf(otHours));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // Notification
    private void showJobFinishedNotification() {
        androidx.core.app.NotificationCompat.Builder builder =
                new androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle("Job Completed")
                        .setContentText("This job was successfully completed and logged.")
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        androidx.core.app.NotificationManagerCompat notificationManager =
                androidx.core.app.NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1001, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Job Finish Channel";
            String description = "Notifications for finished jobs";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void logVolleyError(VolleyError error, String contextMessage) {
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            String body = new String(error.networkResponse.data);
            Log.e("VolleyError", contextMessage + " | Status: " + statusCode + " | Body: " + body);
        } else {
            Log.e("VolleyError", contextMessage + " | " + error.toString());
        }
        Toast.makeText(this, contextMessage, Toast.LENGTH_LONG).show();
    }
}
