package com.example.hire_me_test.view.actvities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;
import com.example.hire_me_test.view.adaptors.WithdrawRequestAdapter;
import com.example.hire_me_test.model.model.data.WithdrawRequestModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WithdrawRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerRequests;
    private WithdrawRequestAdapter adapter;
    private List<WithdrawRequestModel> requestList = new ArrayList<>();
    private String employerId;
    private static final String BASE_URL = "https://hireme.cpsharetxt.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_request);

        recyclerRequests = findViewById(R.id.recyclerRequests);
        recyclerRequests.setLayoutManager(new LinearLayoutManager(this));

        employerId = getIntent().getStringExtra("employer_id");

        adapter = new WithdrawRequestAdapter(requestList, this::updateRequestStatus);
        recyclerRequests.setAdapter(adapter);

        fetchRequests();
    }

    private void fetchRequests() {
        String url = BASE_URL + "get_withdraw_requests.php?employer_id=" + employerId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    requestList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            requestList.add(new WithdrawRequestModel(
                                    obj.getInt("id"),
                                    obj.getString("idNumber"),
                                    obj.getInt("job_id"),
                                    obj.getDouble("amount"),
                                    obj.getString("status"),
                                    obj.getString("requested_at")
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void updateRequestStatus(int requestId, String newStatus) {
        String url = BASE_URL + "update_withdraw_status.php?id=" + requestId + "&status=" + newStatus;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    fetchRequests();
                },
                error -> Toast.makeText(this, "Error updating status", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
