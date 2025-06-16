package com.example.hire_me_test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EmpProfileActivity extends AppCompatActivity {

    EditText idNumberEditText, fullNameEditText, usernameEditText, emailEditText,
            contactEditText, permanentAddressEditText, currentAddressEditText,
            workExperienceEditText, bankAccountEditText, bankNameEditText, bankBranchEditText;
    TextView greetingText;
    Button saveProfileBtn;

    String idNumber; // From login session or intent
    String URL_GET = "https://hireme.cpsharetxt.com/get_profile.php";
    String URL_UPDATE = "https://hireme.cpsharetxt.com/update_profile.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_profile);

        idNumber = getIntent().getStringExtra("id_number"); // Get from login

        greetingText = findViewById(R.id.greetingText);
        idNumberEditText = findViewById(R.id.idNumberEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        contactEditText = findViewById(R.id.contactEditText);
        permanentAddressEditText = findViewById(R.id.permanentAddressEditText);
        currentAddressEditText = findViewById(R.id.currentAddressEditText);
        workExperienceEditText = findViewById(R.id.workExperienceEditText);
        bankAccountEditText = findViewById(R.id.bankAccountEditText);
        bankNameEditText = findViewById(R.id.bankNameEditText);
        bankBranchEditText = findViewById(R.id.bankBranchEditText);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);

        fetchProfile();

        saveProfileBtn.setOnClickListener(v -> updateProfile());
    }

    private void fetchProfile() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("status").equals("success")) {
                            JSONObject user = obj.getJSONObject("user");
                            greetingText.setText("Hi Mr. " + user.getString("full_name"));
                            idNumberEditText.setText(user.getString("id_number"));
                            fullNameEditText.setText(user.getString("full_name"));
                            usernameEditText.setText(user.getString("username"));
                            emailEditText.setText(user.getString("email"));
                            contactEditText.setText(user.getString("contact_number"));
                            permanentAddressEditText.setText(user.getString("permanent_address"));
                            currentAddressEditText.setText(user.getString("current_address"));
                            workExperienceEditText.setText(user.getString("work_experience"));
                            bankAccountEditText.setText(user.getString("bank_account_number"));
                            bankNameEditText.setText(user.getString("bank_name"));
                            bankBranchEditText.setText(user.getString("bank_branch"));
                        } else {
                            Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_number", idNumber);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void updateProfile() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE,
                response -> Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Update error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_number", idNumberEditText.getText().toString());
                params.put("full_name", fullNameEditText.getText().toString());
                params.put("username", usernameEditText.getText().toString());
                params.put("email", emailEditText.getText().toString());
                params.put("contact_number", contactEditText.getText().toString());
                params.put("permanent_address", permanentAddressEditText.getText().toString());
                params.put("current_address", currentAddressEditText.getText().toString());
                params.put("work_experience", workExperienceEditText.getText().toString());
                params.put("bank_account_number", bankAccountEditText.getText().toString());
                params.put("bank_name", bankNameEditText.getText().toString());
                params.put("bank_branch", bankBranchEditText.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}