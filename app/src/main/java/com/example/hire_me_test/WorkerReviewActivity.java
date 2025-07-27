package com.example.hire_me_test;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkerReviewActivity extends AppCompatActivity {

    RecyclerView reviewRecyclerView;
    TextView txtNoReviews;
    ArrayList<Review> reviewList;
    String idNumber;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_review);

        idNumber = getIntent().getStringExtra("id_number");

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        txtNoReviews = findViewById(R.id.txtNoReviews);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> onBackPressed());

        reviewList = new ArrayList<>();
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadReviews();
    }

    private void loadReviews() {
        String url = "https://hireme.cpsharetxt.com/get_worker_reviews.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray reviewsArray = jsonObject.getJSONArray("reviews");

                            if (reviewsArray.length() == 0) {
                                txtNoReviews.setVisibility(View.VISIBLE);
                            } else {
                                txtNoReviews.setVisibility(View.GONE);
                                for (int i = 0; i < reviewsArray.length(); i++) {
                                    JSONObject obj = reviewsArray.getJSONObject(i);
                                    Review review = new Review(
                                            obj.getString("rated_by"),
                                            obj.getInt("rating"),
                                            obj.getString("work_experience"),
                                            obj.getString("feedback"),
                                            obj.getString("job_title"),
                                            obj.getString("company_name"),
                                            obj.getString("duration"),
                                            obj.getString("created_at")
                                    );
                                    reviewList.add(review);
                                }
                                reviewRecyclerView.setAdapter(new ReviewAdapter(WorkerReviewActivity.this, reviewList));
                            }
                        } else {
                            Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("id_number", idNumber);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
