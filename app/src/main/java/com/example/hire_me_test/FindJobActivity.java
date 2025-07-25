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

import java.util.HashMap;
import java.util.Map;

public class FindJobActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;  // Using these because your XML uses those IDs
    private Button buttonLogin, buttonNewUser;
    private TextView textViewForgotPassword;

    // PHP URL where login is checked
    private static final String LOGIN_URL = "https://hireme.cpsharetxt.com/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job); // 🔁 Replace with your actual layout file name

        // Match XML views
        editTextEmail = findViewById(R.id.editTextEmail);       // used as ID number field
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonNewUser = findViewById(R.id.buttonNewUser);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        // Login button click
        buttonLogin.setOnClickListener(v -> {
            String idNumber = editTextEmail.getText().toString().trim();  // treated as ID number
            String password = editTextPassword.getText().toString().trim();

            if (idNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter ID number and password", Toast.LENGTH_SHORT).show();
            } else {
                checkLogin(idNumber, password);
            }
        });

        // Create New User
        buttonNewUser.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Forgot Password
        textViewForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    private void checkLogin(String idNumber, String password) {
        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    if (response.contains("success")) {
                        Intent intent = new Intent(this, EmpProfileActivity.class);
                        intent.putExtra("id_number", idNumber);  // Pass ID number if needed
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid ID number or password", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_number", idNumber);   // <-- Send as ID number
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
