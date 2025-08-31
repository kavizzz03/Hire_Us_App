package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.view.adaptors.JobAdapter;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.Job;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class JobListActivity extends AppCompatActivity implements JobAdapter.OnJobClickListener {

    private RecyclerView recyclerJobs;
    private EditText editSearch;
    private Spinner spinnerDateFilter;
    private JobAdapter adapter;
    private List<Job> jobList;
    private List<Job> filteredList;
    private String idNumber;
    ImageView btnBack;

    private static final String URL = "https://hireme.cpsharetxt.com/get_jobs.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        idNumber = getIntent().getStringExtra("id_number");

        recyclerJobs = findViewById(R.id.recyclerJobs);
        recyclerJobs.setLayoutManager(new LinearLayoutManager(this));

        editSearch = findViewById(R.id.editSearch);
        spinnerDateFilter = findViewById(R.id.spinnerDateFilter);

        jobList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new JobAdapter(filteredList, this);
        recyclerJobs.setAdapter(adapter);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        loadJobs();

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });

        spinnerDateFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                applyFilters();
            }

            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void loadJobs() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                response -> {
                    try {
                        JSONArray jobArray = new JSONArray(response);
                        jobList.clear();

                        Set<String> uniqueDates = new TreeSet<>(); // Automatically sorted

                        for (int i = 0; i < jobArray.length(); i++) {
                            JSONObject jobObj = jobArray.getJSONObject(i);

                            int id = jobObj.getInt("id");
                            String jobTitle = jobObj.getString("job_title");
                            String companyName = jobObj.getString("company_name");
                            String vacancies = jobObj.optString("vacancies", "N/A");
                            String timeRange = jobObj.getString("time_range");
                            String location = jobObj.getString("location");
                            String basicSalary = jobObj.getString("basic_salary");
                            String otSalary = jobObj.optString("ot_salary", "0.00");
                            String requirements = jobObj.optString("requirements", "");
                            String jobDate = jobObj.getString("job_date");
                            String pickupLocation = jobObj.optString("pickup_location", "");
                            String contactInfo = jobObj.getString("contact_info");
                            String email = jobObj.getString("email");

                            uniqueDates.add(jobDate);

                            jobList.add(new Job(id, jobTitle, companyName, vacancies, timeRange,
                                    location, basicSalary, otSalary, requirements, jobDate,
                                    pickupLocation, contactInfo, email));
                        }

                        List<String> dateOptions = new ArrayList<>();
                        dateOptions.add("All Dates");
                        dateOptions.addAll(uniqueDates);

                        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dateOptions);
                        spinnerDateFilter.setAdapter(dateAdapter);
                        spinnerDateFilter.setSelection(0); // default to "All Dates"

                        applyFilters();

                        Toast.makeText(this, "Jobs loaded: " + jobList.size(), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to parse jobs", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to load jobs", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void applyFilters() {
        String searchQuery = editSearch.getText().toString().trim().toLowerCase();
        String selectedDate = spinnerDateFilter.getSelectedItem() != null
                ? spinnerDateFilter.getSelectedItem().toString()
                : "All Dates";

        filteredList.clear();

        for (Job job : jobList) {
            boolean matchesSearch = job.getJobTitle().toLowerCase().contains(searchQuery);
            boolean matchesDate = selectedDate.equals("All Dates") || job.getJobDate().equals(selectedDate);

            if (matchesSearch && matchesDate) {
                filteredList.add(job);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onJobClick(Job job) {
        Intent intent = new Intent(this, FindJobEmpActivity.class);
        intent.putExtra("id_number", idNumber);
        intent.putExtra("job_id", job.getId());
        intent.putExtra("job_title", job.getJobTitle());
        intent.putExtra("company_name", job.getCompanyName());
        intent.putExtra("vacancies", job.getVacancies());
        intent.putExtra("time_range", job.getTimeRange());
        intent.putExtra("location", job.getLocation());
        intent.putExtra("basic_salary", job.getBasicSalary());
        intent.putExtra("ot_salary", job.getOtSalary());
        intent.putExtra("requirements", job.getRequirements());
        intent.putExtra("job_date", job.getJobDate());
        intent.putExtra("pickup_location", job.getPickupLocation());
        intent.putExtra("contact_info", job.getContactInfo());
        intent.putExtra("email", job.getEmail());

        startActivity(intent);
    }
}
