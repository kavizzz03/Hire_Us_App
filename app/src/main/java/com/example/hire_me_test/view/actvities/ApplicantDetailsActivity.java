package com.example.hire_me_test.view.actvities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApplicantDetailsActivity extends AppCompatActivity {

    TextView txtFullName, txtEmail, txtContact, txtAddress, txtExperience, txtBankInfo, txtReviewSummary, txtReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_details);

        txtFullName = findViewById(R.id.txtDetailFullName);
        txtEmail = findViewById(R.id.txtDetailEmail);
        txtContact = findViewById(R.id.txtDetailContact);
        txtAddress = findViewById(R.id.txtDetailAddress);
        txtExperience = findViewById(R.id.txtDetailExperience);
        txtBankInfo = findViewById(R.id.txtDetailBank);
        txtReviewSummary = findViewById(R.id.txtReviewSummary);
        txtReviews = findViewById(R.id.txtDetailReviews);

        String idNumber = getIntent().getStringExtra("idNumber");
        if (idNumber == null || idNumber.isEmpty()) {
            Toast.makeText(this, "Invalid worker ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadWorkerDetails(idNumber);
    }

    private void loadWorkerDetails(String idNumber) {
        String url = "https://hireme.cpsharetxt.com/get_worker_details.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.optBoolean("success", false)) {
                            Toast.makeText(this, jsonObject.optString("message", "Error loading worker details"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject worker = jsonObject.getJSONObject("worker");

                        txtFullName.setText(worker.optString("fullName"));
                        txtEmail.setText(worker.optString("email"));
                        txtContact.setText(worker.optString("contactNumber"));

                        String address = "Permanent: " + worker.optString("permanentAddress") + "\nCurrent: " + worker.optString("currentAddress");
                        txtAddress.setText(address);

                        txtExperience.setText(worker.optString("workExperience"));

                        String bankInfo = "Bank: " + worker.optString("bankName") +
                                "\nAccount No: " + worker.optString("bankAccountNumber") +
                                "\nBranch: " + worker.optString("bankBranch");
                        txtBankInfo.setText(bankInfo);

                        // Process reviews
                        JSONArray reviews = jsonObject.getJSONArray("reviews");
                        StringBuilder reviewsText = new StringBuilder();
                        int totalRating = 0;

                        for (int i = 0; i < reviews.length(); i++) {
                            JSONObject r = reviews.getJSONObject(i);
                            int rating = r.optInt("rating", 0);
                            totalRating += rating;

                            reviewsText.append("Rating: ").append(rating).append("/5\n");
                            reviewsText.append("By: ").append(r.optString("rated_by")).append("\n");
                            reviewsText.append("Feedback: ").append(r.optString("feedback_text")).append("\n");
                            reviewsText.append("Job: ").append(r.optString("job_title")).append("\n");
                            reviewsText.append("Company: ").append(r.optString("company_name")).append("\n");
                            reviewsText.append("Duration: ").append(r.optString("duration")).append("\n");
                            reviewsText.append("-----\n");
                        }

                        int totalReviews = reviews.length();
                        float averageRating = totalReviews > 0 ? (float) totalRating / totalReviews : 0;

                        txtReviewSummary.setText("Reviews: " + totalReviews + " | Average Rating: " + String.format("%.2f", averageRating) + "/5");
                        txtReviews.setText(reviewsText.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Loading Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("id_number", idNumber);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
