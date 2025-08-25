package com.example.hire_me_test.view.actvities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.WorkerApplication;
import com.example.hire_me_test.view.adaptors.WorkerApplicationsAdapter;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkerApplicationsActivity extends AppCompatActivity {

    RecyclerView rvApplications;
    TextView tvEmpty;
    WorkerApplicationsAdapter adapter;
    List<WorkerApplication> applicationList = new ArrayList<>();
    String workerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_applications);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarApplications);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvApplications = findViewById(R.id.rvApplications);
        tvEmpty = findViewById(R.id.tvEmpty);
        rvApplications.setLayoutManager(new LinearLayoutManager(this));

        workerId = getIntent().getStringExtra("id_number");

        adapter = new WorkerApplicationsAdapter(this, applicationList, workerId);
        rvApplications.setAdapter(adapter);

        fetchApplications();
    }

    private void fetchApplications() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("id_number", workerId)
                .build();

        Request request = new Request.Builder()
                .url("https://hireme.cpsharetxt.com/get_worker_applications.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(WorkerApplicationsActivity.this, "Failed to load applications", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject obj = new JSONObject(res);
                    runOnUiThread(() -> applicationList.clear());

                    if (obj.getString("status").equals("success")) {
                        JSONArray arr = obj.getJSONArray("applications");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject a = arr.getJSONObject(i);
                            WorkerApplication application = new WorkerApplication(
                                    a.getInt("id"),
                                    a.getString("job_id"),
                                    a.getString("job_title"),
                                    a.getString("company_name"),
                                    a.getString("applied_at")
                            );
                            applicationList.add(application);
                        }

                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            tvEmpty.setVisibility(applicationList.isEmpty() ? TextView.VISIBLE : TextView.GONE);
                        });
                    } else {
                        runOnUiThread(() -> tvEmpty.setVisibility(TextView.VISIBLE));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> tvEmpty.setVisibility(TextView.VISIBLE));
                }
            }
        });
    }
}
