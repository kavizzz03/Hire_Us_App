package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.Glide;
import com.example.hire_me_test.R;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button makeJobBtn, jobHistoryBtn, logoutBtn, withdrawRequestBtn, editJobBtn, chatBtn, editProfileBtn;
    private ShapeableImageView companyIcon;
    private String userId;
    private final String phpUrl = "https://hireme.cpsharetxt.com/get_company_icon.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Init views
        welcomeText = findViewById(R.id.welcomeText);
        makeJobBtn = findViewById(R.id.makeJobBtn);
        jobHistoryBtn = findViewById(R.id.jobHistoryBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        withdrawRequestBtn = findViewById(R.id.withdrawRequestBtn);
        editJobBtn = findViewById(R.id.editJobBtn);
        chatBtn = findViewById(R.id.chatBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        companyIcon = findViewById(R.id.companyIconImage);

        // Get userId
        userId = getIntent().getStringExtra("user_id");
        if (userId != null && !userId.isEmpty()) fetchEmployerData(userId);

        // Button listeners
        makeJobBtn.setOnClickListener(v -> openActivity(PostJobActivity.class, "employer_id", userId));
        jobHistoryBtn.setOnClickListener(v -> openActivity(EmployerJobsHistoryActivity.class, "employer_id", userId));
        withdrawRequestBtn.setOnClickListener(v -> openActivity(WithdrawRequestActivity.class, "employer_id", userId));
        editJobBtn.setOnClickListener(v -> openActivity(EmployerJobsActivity.class, "employer_id", userId));
        editProfileBtn.setOnClickListener(v -> openActivity(EmployerProfileActivity.class, "employer_id", userId));
        chatBtn.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        logoutBtn.setOnClickListener(v -> logout());
    }

    private void openActivity(Class<?> cls, String key, String value) {
        if (value == null || value.isEmpty()) return;
        Intent intent = new Intent(this, cls);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    private void logout() {
        Intent intent = new Intent(this, GiveJobActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void fetchEmployerData(String userId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try { postData.put("user_id", userId); } catch (JSONException e) { e.printStackTrace(); }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, phpUrl, postData,
                response -> {
                    String status = response.optString("status", "error");
                    if (status.equals("success")) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            String companyName = data.optString("company_name", "Employer");
                            String companyIconUrl = data.optString("company_icon_url", "");

                            welcomeText.setText("Welcome, " + companyName);

                            Glide.with(this)
                                    .load(companyIconUrl)
                                    .placeholder(R.drawable.outline_profile_24)
                                    .error(R.drawable.outline_profile_24)
                                    .circleCrop()
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(companyIcon);
                        }
                    } else {
                        welcomeText.setText("Welcome, Employer");
                        companyIcon.setImageResource(R.drawable.outline_profile_24);
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    companyIcon.setImageResource(R.drawable.outline_profile_24);
                });

        queue.add(request);
    }
}
