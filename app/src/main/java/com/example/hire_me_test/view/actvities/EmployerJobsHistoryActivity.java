package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.JobHistory;
import com.example.hire_me_test.view.adaptors.JobHistoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class EmployerJobsHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<JobHistory> jobList;
    JobHistoryAdapter adapter;
    String employerId;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_jobs_history);

        // Back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());  // Go back when clicked

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewJobHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();

        // Get employer ID from intent
        employerId = getIntent().getStringExtra("employer_id");

        // Fetch jobs
        fetchJobs();
    }

    private void fetchJobs() {
        String url = "https://hireme.cpsharetxt.com/get_employer_jobs_history.php?employer_id=" + employerId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jobsArray = new JSONArray(response);
                        for (int i = 0; i < jobsArray.length(); i++) {
                            JSONObject obj = jobsArray.getJSONObject(i);
                            JobHistory job = new JobHistory(
                                    obj.getInt("job_id"),
                                    obj.getString("job_title"),
                                    obj.getString("date"),
                                    obj.getString("location")
                            );
                            jobList.add(job);
                        }

                        adapter = new JobHistoryAdapter(jobList, job -> {
                            Intent intent = new Intent(this, JobParticipantsActivity.class);
                            intent.putExtra("job_id", job.getJobId());
                            startActivity(intent);
                        });
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
