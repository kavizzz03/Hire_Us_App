package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FindJobActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonNewUser;
    private TextView textViewForgotPassword;

    // PHP login URL
    private static final String LOGIN_URL = "https://hireme.cpsharetxt.com/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job); // Make sure this is your correct layout file

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail);   // used for ID number
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonNewUser = findViewById(R.id.buttonNewUser);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        // Login button
        buttonLogin.setOnClickListener(v -> {
            String idNumber = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (idNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter ID number and password", Toast.LENGTH_SHORT).show();
            } else {
                checkLogin(idNumber, password);
            }
        });

        // Register button
        buttonNewUser.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Forgot password
        textViewForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ResetRequestActivity.class));
        });
    }

    private void checkLogin(String idNumber, String password) {
        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equals("success")) {
                            // Get values from JSON
                            String fullName = jsonObject.getString("full_name");
                            String jobTitle = jsonObject.getString("job_title");

                            // Start profile activity and pass data
                            Intent intent = new Intent(this, EmpProfileActivity.class);
                            intent.putExtra("id_number", idNumber);
                            intent.putExtra("full_name", fullName);
                            intent.putExtra("job_title", jobTitle);
                           startActivity(intent);
                            finish();
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Response parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_number", idNumber);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
