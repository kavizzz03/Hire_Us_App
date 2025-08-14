package com.example.hire_me_test.view.actvities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.hire_me_test.R;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailInput;
    Button sendEmailBtn;
    ProgressDialog progressDialog;

    String URL = "https://hireme.cpsharetxt.com/forgot_password.php"; // Replace with your real PHP URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.hire_me_test.R.layout.activity_forgot_password);

        emailInput = findViewById(com.example.hire_me_test.R.id.emailInput);
        sendEmailBtn = findViewById(R.id.sendEmailBtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        sendEmailBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Enter a valid email");
            } else {
                sendResetRequest(email);
            }
        });
    }

    private void sendResetRequest(String email) {
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    progressDialog.dismiss();
                    switch (response) {
                        case "success":
                            Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, GiveJobActivity.class);
                            startActivity(intent);
                            finish(); // Optional: close ForgotPasswordActivity
                            break;  // <---- fixed here: no parentheses
                        case "not_found":
                            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(this, "Server error: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Volley Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
