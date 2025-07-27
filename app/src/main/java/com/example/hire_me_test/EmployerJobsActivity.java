package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class EmployerJobsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EmployerJobsAdapter adapter;
    ArrayList<JobModel> jobList = new ArrayList<>();
    String employerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_jobs);

        employerId = getIntent().getStringExtra("employer_id");
        recyclerView = findViewById(R.id.recyclerJobs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EmployerJobsAdapter(this, jobList, job -> {
            Intent intent = new Intent(this, ApplicantsActivity.class);
            intent.putExtra("job_id", job.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        fetchJobs();
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
}
