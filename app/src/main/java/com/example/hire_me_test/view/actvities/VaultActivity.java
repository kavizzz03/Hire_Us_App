package com.example.hire_me_test.view.actvities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;
import com.example.hire_me_test.view.adaptors.VaultAdapter;
import com.example.hire_me_test.model.model.data.VaultModel;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VaultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VaultAdapter adapter;
    private ArrayList<VaultModel> vaultList;
    private TextView totalAmountText;
    private String workerIdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);

        recyclerView = findViewById(R.id.recyclerView);
        totalAmountText = findViewById(R.id.totalAmountText);

        vaultList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        workerIdNumber = getIntent().getStringExtra("id_number");
        if (workerIdNumber == null || workerIdNumber.isEmpty()) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());


        adapter = new VaultAdapter(this, vaultList, workerIdNumber);
        recyclerView.setAdapter(adapter);

        loadVaultData(workerIdNumber);
    }


    private void loadVaultData(String idNumber) {
        String url = "https://hireme.cpsharetxt.com/get_vault.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // Try parsing as JSON array first
                        JSONArray jsonArray = new JSONArray(response);
                        double total = 0;

                        vaultList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            String jobId = obj.getString("job_id");
                            double salary = obj.getDouble("salary");
                            double otHours = obj.getDouble("ot_hours");
                            double otSalary = obj.getDouble("ot_salary");
                            String updatedAt = obj.getString("updated_at");

                            total += salary + otSalary;

                            VaultModel model = new VaultModel(jobId, salary, otHours, otSalary, updatedAt);
                            vaultList.add(model);
                        }

                        totalAmountText.setText("Total Amount: Rs. " + total);
                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        // If not array, maybe it's an error object
                        try {
                            JSONObject json = new JSONObject(response);

                            if (json.has("already_requested")) {
                                Toast.makeText(VaultActivity.this, json.getString("already_requested"), Toast.LENGTH_LONG).show();
                            } else if (json.has("error")) {
                                Toast.makeText(VaultActivity.this, json.getString("error"), Toast.LENGTH_LONG).show();
                            } else if (json.has("warning")) {
                                Toast.makeText(VaultActivity.this, json.getString("warning"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(VaultActivity.this, "Unknown response received", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception jsonEx) {
                            jsonEx.printStackTrace();
                            Toast.makeText(VaultActivity.this, "Parsing error (invalid format).", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(VaultActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idNumber", idNumber);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
