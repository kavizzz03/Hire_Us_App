package com.example.hire_me_test.view.actvities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class OrderFoodActivity extends AppCompatActivity {

    private static final String TAG = "OrderFoodActivity";

    private String idNumber, jobId;
    private TextView txtMealInfo;
    private Button btnYesMeal, btnNoMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_food);

        txtMealInfo = findViewById(R.id.txtMealInfo);
        btnYesMeal = findViewById(R.id.btnYesMeal);
        btnNoMeal = findViewById(R.id.btnNoMeal);

        // Hide buttons initially
        btnYesMeal.setVisibility(View.GONE);
        btnNoMeal.setVisibility(View.GONE);

        // Retrieve ID from intent
        idNumber = getIntent().getStringExtra("idNumber");
        if (idNumber == null) {
            idNumber = getIntent().getStringExtra("id_number");
        }

        if (idNumber == null || idNumber.trim().isEmpty()) {
            Toast.makeText(this, "❌ Worker ID not provided!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d(TAG, "Received idNumber: " + idNumber);
        checkIfHired();
    }

    private void checkIfHired() {
        String url = "https://hireme.cpsharetxt.com/check_worker_hired.php?id_number=" + idNumber;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "checkIfHired response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean exists = obj.optBoolean("exists", false);

                        if (!exists) {
                            txtMealInfo.setText("❌ Worker not found.");
                            return;
                        }

                        if (obj.optBoolean("hired", false)) {
                            jobId = obj.optString("job_id", "");
                            Log.d(TAG, "Worker is hired for job ID: " + jobId);
                            fetchMeals(jobId);
                        } else {
                            txtMealInfo.setText("❌ You are not hired for any job yet.");
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing error in checkIfHired()", e);
                        txtMealInfo.setText("❗ Error parsing server response.");
                    }
                },
                error -> {
                    Log.e(TAG, "Network error in checkIfHired()", error);
                    txtMealInfo.setText("⚠️ Network error. Please check your connection.");
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchMeals(String jobId) {
        String url = "https://hireme.cpsharetxt.com/meals.php?job_id=" + jobId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "fetchMeals response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean success = obj.optBoolean("success", false);
                        JSONArray meals = obj.optJSONArray("meals");

                        if (success && meals != null && meals.length() > 0) {
                            StringBuilder mealDetails = new StringBuilder("🍱 Meals available:\n\n");
                            for (int i = 0; i < meals.length(); i++) {
                                JSONObject meal = meals.getJSONObject(i);
                                String name = meal.optString("meal_name", "Unnamed Meal");
                                String desc = meal.optString("description", "No description");
                                String price = meal.optString("meal_price", "0.00");

                                mealDetails.append("🍽️ ").append(name)
                                        .append("\n📋 ").append(desc)
                                        .append("\n💰 Rs. ").append(price)
                                        .append("\n\n");
                            }

                            txtMealInfo.setText(mealDetails.toString().trim());
                            btnYesMeal.setVisibility(View.VISIBLE);
                            btnNoMeal.setVisibility(View.VISIBLE);

                            btnYesMeal.setOnClickListener(v -> updateMealPreference("yes"));
                            btnNoMeal.setOnClickListener(v -> updateMealPreference("no"));
                        } else {
                            txtMealInfo.setText("🚫 No meals available for your job.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing meals response", e);
                        txtMealInfo.setText("❗ Failed to read meal data.");
                    }
                },
                error -> {
                    Log.e(TAG, "Network error in fetchMeals()", error);
                    txtMealInfo.setText("⚠️ Could not fetch meals. Try again later.");
                });

        Volley.newRequestQueue(this).add(request);
    }



    private void updateMealPreference(String choice) {
        String url = "https://hireme.cpsharetxt.com/update_meals_choice.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "updateMealPreference response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success", false)) {
                            Toast.makeText(this, "✅ Your meal choice was saved!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "❌ Failed to save meal choice.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing updateMealPreference response", e);
                        Toast.makeText(this, "❗ Failed to process update.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error in updateMealPreference()", error);
                    Toast.makeText(this, "⚠️ Network error during update.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("id_number", idNumber);
                map.put("job_id", jobId);
                map.put("wants_meals", choice);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
