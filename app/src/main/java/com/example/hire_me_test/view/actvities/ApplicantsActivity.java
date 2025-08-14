package com.example.hire_me_test.view.actvities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.view.adaptors.ApplicantAdapter;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.Worker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplicantsActivity extends AppCompatActivity {

    private RecyclerView recyclerApplicants;
    private ArrayList<Worker> applicantList;
    private ApplicantAdapter adapter;
    private int jobId, vacancies;
    private Button btnSelectApplicants;

    // Store already hired worker idNumbers to disable selection
    private final Set<String> alreadyHired = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants);

        recyclerApplicants = findViewById(R.id.recyclerApplicants);
        recyclerApplicants.setLayoutManager(new LinearLayoutManager(this));
        btnSelectApplicants = findViewById(R.id.btnSelectApplicants);

        jobId = getIntent().getIntExtra("job_id", -1);
        vacancies = getIntent().getIntExtra("vacancies", 1);

        if (jobId == -1) {
            Toast.makeText(this, "Invalid job ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        applicantList = new ArrayList<>();
        adapter = new ApplicantAdapter(this, applicantList, vacancies, alreadyHired, this::onWorkerClicked);
        recyclerApplicants.setAdapter(adapter);

        loadApplicants();

        btnSelectApplicants.setOnClickListener(v -> saveSelectedApplicants());
    }

    private void loadApplicants() {
        String url = "https://hireme.cpsharetxt.com/load_applicants.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        boolean success = json.optBoolean("success", false);
                        if (!success) {
                            Toast.makeText(this, json.optString("message", "No applicants found"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update vacancies count from server (if provided)
                        vacancies = json.optInt("vacancies", vacancies);
                        adapter.setVacancies(vacancies);

                        // Clear and refill the already hired set
                        alreadyHired.clear();
                        JSONArray hiredArray = json.optJSONArray("already_hired");
                        if (hiredArray != null) {
                            for (int i = 0; i < hiredArray.length(); i++) {
                                alreadyHired.add(hiredArray.getString(i));
                            }
                        }

                        // Load applicant list
                        JSONArray arr = json.getJSONArray("applicants");
                        applicantList.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);

                            JSONArray fbArray = obj.optJSONArray("feedback");
                            StringBuilder sb = new StringBuilder();
                            if (fbArray != null) {
                                for (int j = 0; j < fbArray.length(); j++) {
                                    if (j > 0) sb.append("\n---\n");
                                    sb.append(fbArray.getString(j));
                                }
                            }

                            Worker w = new Worker(
                                    obj.optString("idNumber"),
                                    obj.optString("fullName"),
                                    obj.optString("email"),
                                    obj.optString("status"),
                                    obj.optString("contactNumber"),
                                    obj.optString("workExperience"),
                                    obj.optString("rating"),
                                    sb.toString()
                            );

                            applicantList.add(w);
                        }

                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Loading Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("job_id", String.valueOf(jobId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void onWorkerClicked(Worker worker) {
        Intent intent = new Intent(this, ApplicantDetailsActivity.class);
        intent.putExtra("idNumber", worker.getIdNumber());
        startActivity(intent);
    }

    private void saveSelectedApplicants() {
        List<Worker> selected = adapter.getSelectedWorkers();

        if (selected.isEmpty()) {
            Toast.makeText(this, "Select at least one applicant", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selected.size() > vacancies) {
            Toast.makeText(this, "You can select up to " + vacancies + " applicants only", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Finalizing selections…");
        pd.setCancelable(false);
        pd.show();

        String url = "https://hireme.cpsharetxt.com/hire_workers.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    pd.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (!json.optBoolean("success", false)) {
                            Toast.makeText(this, json.optString("message"), Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONArray skipped = json.optJSONArray("skipped");
                        if (skipped != null && skipped.length() > 0) {
                            StringBuilder skippedMsg = new StringBuilder();
                            for (int i = 0; i < skipped.length(); i++) {
                                skippedMsg.append(skipped.getString(i)).append("\n");
                            }
                            Toast.makeText(this, "Some skipped:\n" + skippedMsg.toString(), Toast.LENGTH_LONG).show();
                        }

                        int hiredCount = selected.size() - (skipped != null ? skipped.length() : 0);
                        Toast.makeText(this, hiredCount + " hired successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    pd.dismiss();
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("job_id", String.valueOf(jobId));

                // Send selected worker idNumbers as array indices
                for (int i = 0; i < selected.size(); i++) {
                    params.put("id_numbers[" + i + "]", selected.get(i).getIdNumber());
                }
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
