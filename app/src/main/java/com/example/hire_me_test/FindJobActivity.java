package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class FindJobActivity extends AppCompatActivity {

    private TextInputEditText idNumberEditText, passwordEditText;
    private Button loginBtn, newUserBtn, forgotPasswordBtn;

    // Replace with your real URL for login validation PHP script
    private static final String LOGIN_URL = "https://hireme.cpsharetxt.com/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job); // Your layout name

        idNumberEditText = findViewById(R.id.idNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginBtn);
        newUserBtn = findViewById(R.id.newUserBtn);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);

        loginBtn.setOnClickListener(view -> {
            String idNumber = idNumberEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (idNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(FindJobActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                checkLogin(idNumber, password);
            }
        });

        newUserBtn.setOnClickListener(view -> {
            startActivity(new Intent(FindJobActivity.this, RegisterActivity.class));
        });

        forgotPasswordBtn.setOnClickListener(view -> {
            Toast.makeText(FindJobActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            // Implement forgot password activity start here
        });
    }

    private void checkLogin(String idNumber, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    // Assuming your PHP returns a JSON with {"status":"success"} or {"status":"error"}
                    if (response.contains("success")) {
                        // Login success - open EmpProfileActivity
                        Intent intent = new Intent(FindJobActivity.this, EmpProfileActivity.class);
                        intent.putExtra("id_number", idNumber); // pass ID number to profile if needed
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(FindJobActivity.this, "Invalid ID number or password", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(FindJobActivity.this, "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Send POST parameters to PHP
                Map<String, String> params = new HashMap<>();
                params.put("id_number", idNumber);
                params.put("password", password);
                return params;
            }
        };

        // Add request to your Volley request queue singleton
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
