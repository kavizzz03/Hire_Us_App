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

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class WithdrawRequestActivity extends AppCompatActivity {

    RecyclerView rv;
    WithdrawRequestAdapter adapter;
    List<WithdrawRequest> requests = new ArrayList<>();
    String employerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_request);

        rv = findViewById(R.id.rvWithdrawRequests);
        rv.setLayoutManager(new LinearLayoutManager(this));

        employerId = getIntent().getStringExtra("employer_id");

        fetchWithdrawRequests();
    }

    private void fetchWithdrawRequests() {
        String url = "https://hireme.cpsharetxt.com/get_withdraw_requests.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.getString("status").equals("success")){
                            requests.clear();
                            JSONArray data = obj.getJSONArray("data");
                            for(int i=0;i<data.length();i++){
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
                            adapter = new WithdrawRequestAdapter(requests, this);
                            rv.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected java.util.Map<String,String> getParams(){
                java.util.Map<String,String> params = new java.util.HashMap<>();
                params.put("employer_id", employerId);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
