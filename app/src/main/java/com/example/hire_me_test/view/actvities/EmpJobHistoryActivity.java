package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.JobHistoryModel;
import com.example.hire_me_test.view.adaptors.EmpJobHistoryAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EmpJobHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmpJobHistoryAdapter adapter;
    private ArrayList<JobHistoryModel> jobHistoryList = new ArrayList<>();
    private String idNumber;
    private OkHttpClient client = new OkHttpClient();
    private static final String URL = "https://hireme.cpsharetxt.com/get_job_history.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_job_history);

        recyclerView = findViewById(R.id.recyclerViewJobHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EmpJobHistoryAdapter(jobHistoryList);
        recyclerView.setAdapter(adapter);

        // ✅ Set click listener AFTER setting adapter
        adapter.setOnItemClickListener(job -> {
            Intent intent = new Intent(EmpJobHistoryActivity.this, JobDetailActivity.class);
            intent.putExtra("job_id", job.getJobId());
            startActivity(intent);
        });

        idNumber = getIntent().getStringExtra("id_number");
        if (idNumber != null && !idNumber.isEmpty()) {
            loadJobHistory(idNumber);
        } else {
            Toast.makeText(this, "ID number is missing!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadJobHistory(String idNumber) {
        String urlWithParam = URL + "?id_number=" + idNumber;

        Request request = new Request.Builder()
                .url(urlWithParam)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(EmpJobHistoryActivity.this,
                        "Network Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    if (json.getString("status").equalsIgnoreCase("success")) {
                        JSONArray jobsArray = json.getJSONArray("jobs");
                        jobHistoryList.clear();

                        for (int i = 0; i < jobsArray.length(); i++) {
                            JSONObject job = jobsArray.getJSONObject(i);
                            jobHistoryList.add(new JobHistoryModel(
                                    job.getString("job_id"),
                                    job.getString("hired_at"),
                                    job.optString("wants_meals", "no")
                            ));
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } else {
                        runOnUiThread(() -> Toast.makeText(EmpJobHistoryActivity.this,
                                json.optString("message", "Failed to load data"), Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(EmpJobHistoryActivity.this,
                            "Error parsing data", Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }
            }
        });
    }
}
