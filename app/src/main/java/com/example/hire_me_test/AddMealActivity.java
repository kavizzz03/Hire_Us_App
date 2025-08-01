package com.example.hire_me_test;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class AddMealActivity extends AppCompatActivity {

    EditText edtMealName, edtDescription, edtPrice;
    Button btnSaveMeal,logoutBtn;
    ImageButton btnBack;
    int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        // Enable ActionBar back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind UI elements
        edtMealName = findViewById(R.id.edtMealName);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        btnSaveMeal = findViewById(R.id.btnSaveMeal);
        btnBack = findViewById(R.id.btnBack); // ImageButton for back

        // Back button listener
        btnBack.setOnClickListener(v -> onBackPressed());

        // Get job ID from Intent
        jobId = getIntent().getIntExtra("job_id", -1);
        if (jobId == -1) {
            Toast.makeText(this, "Job ID is missing!", Toast.LENGTH_LONG).show();
            Log.e("JOB_ID_ERROR", "No job ID passed in intent");
            finish();
            return;
        }

        // Save button click
        btnSaveMeal.setOnClickListener(v -> {
            String name = edtMealName.getText().toString().trim();
            String desc = edtDescription.getText().toString().trim();
            String price = edtPrice.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            saveMealToServer(name, desc, price);
        });
    }

    private void saveMealToServer(String name, String desc, String price) {
        String url = "https://hireme.cpsharetxt.com/add_meal.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Meal Added", Toast.LENGTH_SHORT).show();
                    Log.d("MEAL_RESPONSE", response);
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("MEAL_ERROR", "Volley Error: ", error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("job_id", String.valueOf(jobId));
                params.put("meal_name", name);
                params.put("description", desc);
                params.put("meal_price", price);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // Optional: Handle ActionBar back press
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
