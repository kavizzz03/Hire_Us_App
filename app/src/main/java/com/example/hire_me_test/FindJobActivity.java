package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class FindJobActivity extends AppCompatActivity {

    private EditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button loginBtn, newUserBtn, forgotPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job); // Change if your XML file has a different name

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginBtn);
        newUserBtn = findViewById(R.id.newUserBtn);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);

        // Login Button Click
        loginBtn.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(FindJobActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Implement login logic
                Toast.makeText(FindJobActivity.this, "Login clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // New User Button Click
        newUserBtn.setOnClickListener(view -> {
            Toast.makeText(FindJobActivity.this, "New User clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(FindJobActivity.this, RegisterActivity.class));
        });

        // Forgot Password Button Click
        forgotPasswordBtn.setOnClickListener(view -> {
            Toast.makeText(FindJobActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            // TODO: startActivity(new Intent(FindJobActivity.this, ForgotPasswordActivity.class));
        });
    }
}
