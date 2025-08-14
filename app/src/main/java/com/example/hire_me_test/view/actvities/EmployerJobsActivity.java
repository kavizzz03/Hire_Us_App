package com.example.hire_me_test.view.actvities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.view.adaptors.EmployerJobsAdapter;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.JobModel;

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
                job -> askForOtHoursAndFinishJob(job.getId())
        );

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchJobs();
            swipeRefreshLayout.setRefreshing(false);
        });

        fetchJobs();
        createNotificationChannel();
    }

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
                error -> {
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(this, "Error loading jobs", Toast.LENGTH_LONG).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void askForOtHoursAndFinishJob(int jobId) {
        new AlertDialog.Builder(this)
                .setTitle("Overtime Hours")
                .setMessage("Does this job have OT hours?")
                .setPositiveButton("Yes", (dialog, which) -> showInputOtHoursDialog(jobId))
                .setNegativeButton("No", (dialog, which) -> finishJob(jobId, 0))
                .show();
    }

    private void showInputOtHoursDialog(int jobId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter OT Hours");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("e.g., 2.5");
        builder.setView(input);

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
                error -> {
                    Log.e("FinishJobError", error.toString());
                    Toast.makeText(this, "Error finishing job", Toast.LENGTH_SHORT).show();
                }) {

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

    private void showJobFinishedNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Job Completed")
                .setContentText("This job was successfully completed and logged.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
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
}
