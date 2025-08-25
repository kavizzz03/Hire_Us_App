package com.example.hire_me_test.view.actvities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hire_me_test.R;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JobDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvVacancies, tvSalary, tvLocation, tvEmployer, tvContact;
    private OkHttpClient client = new OkHttpClient();
    private static final String URL = "https://hireme.cpsharetxt.com/get_deleted_job_detail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvVacancies = findViewById(R.id.tvVacancies);
        tvSalary = findViewById(R.id.tvSalary);
        tvLocation = findViewById(R.id.tvLocation);
        tvEmployer = findViewById(R.id.tvEmployer);
        tvContact = findViewById(R.id.tvContact);

        String jobId = getIntent().getStringExtra("job_id");
        if (jobId != null && !jobId.isEmpty()) {
            loadJobDetails(jobId);
        } else {
            Toast.makeText(this, "Job ID missing!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadJobDetails(String jobId) {
        String urlWithParam = URL + "?job_id=" + jobId;

        Request request = new Request.Builder()
                .url(urlWithParam)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(JobDetailActivity.this,
                        "Network Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    if (json.getString("status").equalsIgnoreCase("success")) {
                        JSONObject job = json.getJSONObject("job");
                        JSONObject employer = json.getJSONObject("employer");

                        runOnUiThread(() -> {
                            tvTitle.setText(job.optString("job_title", ""));
                            tvVacancies.setText("Vacancies: " + job.optString("vacancies", ""));
                            tvSalary.setText("Salary: " + job.optString("basic_salary", "") + " + OT: " + job.optString("ot_salary", ""));
                            tvLocation.setText("Location: " + job.optString("location", ""));
                            tvEmployer.setText("Employer: " + employer.optString("company_name", ""));
                            tvContact.setText("Contact: " + employer.optString("contact", ""));
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(JobDetailActivity.this,
                                json.optString("message", "Failed to load job details"), Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(JobDetailActivity.this,
                            "Error parsing data", Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }
            }
        });
    }
}
