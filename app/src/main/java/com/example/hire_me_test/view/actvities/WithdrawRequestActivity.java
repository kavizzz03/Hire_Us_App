package com.example.hire_me_test.view.actvities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.WithdrawRequest;
import com.example.hire_me_test.view.adaptors.WithdrawRequestAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class WithdrawRequestActivity extends AppCompatActivity {

    private RecyclerView rv;
    private WithdrawRequestAdapter adapter;
    private List<WithdrawRequest> requests = new ArrayList<>();
    private String employerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_request);

        // Toolbar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        // RecyclerView
        rv = findViewById(R.id.rvWithdrawRequests);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WithdrawRequestAdapter(requests, this);
        rv.setAdapter(adapter);

        // Get employer ID
        employerId = getIntent().getStringExtra("employer_id");
        if (employerId == null || employerId.isEmpty()) {
            Toast.makeText(this, "Employer ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchWithdrawRequests();
    }

    private void fetchWithdrawRequests() {
        String url = "https://hireme.cpsharetxt.com/get_withdraw_requests.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("success".equals(obj.getString("status"))) {
                            requests.clear();
                            JSONArray data = obj.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.getJSONObject(i);
                                requests.add(new WithdrawRequest(
                                        item.getInt("id"),
                                        item.getInt("job_id"),
                                        item.getString("idNumber"),
                                        item.getDouble("amount"),
                                        item.getString("status"),
                                        item.getString("requested_at")
                                ));
                            }
                            adapter.notifyDataSetChanged(); // Notify adapter after updating list
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Server error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("employer_id", employerId);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
