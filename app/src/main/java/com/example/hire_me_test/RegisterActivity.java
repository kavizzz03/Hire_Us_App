package com.example.hire_me_test;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, username, contactNumber, email, idNumber,
            permanentAddress, currentAddress, workExperience,
            password, confirmPassword;

    Button createAccountBtn, signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Make sure your XML file is named activity_register.xml

        // Binding Views
        fullName = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        contactNumber = findViewById(R.id.contactNumber);
        email = findViewById(R.id.email);
        idNumber = findViewById(R.id.idNumber);
        permanentAddress = findViewById(R.id.permanentAddress);
        currentAddress = findViewById(R.id.currentAddress);
        workExperience = findViewById(R.id.workExperience);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        createAccountBtn = findViewById(R.id.createAccountBtn);
        signInBtn = findViewById(R.id.signInBtn);

        // Create Account Button Click
        createAccountBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                // TODO: Save user data to database
                Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                // You can finish or redirect to login page here
            }
        });

        // Sign In Button Click
        signInBtn.setOnClickListener(v -> {
            // TODO: Redirect to Login Activity
            finish(); // Just closing the current activity for now
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(fullName.getText()) ||
                TextUtils.isEmpty(username.getText()) ||
                TextUtils.isEmpty(contactNumber.getText()) ||
                TextUtils.isEmpty(email.getText()) ||
                TextUtils.isEmpty(idNumber.getText()) ||
                TextUtils.isEmpty(permanentAddress.getText()) ||
                TextUtils.isEmpty(password.getText()) ||
                TextUtils.isEmpty(confirmPassword.getText())) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
