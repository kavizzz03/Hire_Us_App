package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.hire_me_test.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText edtFullName, edtUsername, edtContact, edtPermanent, edtCurrent, edtExperience, edtBankName, edtBranch, edtAccount;
    TextView txtEmail, txtIdNumber;
    Button btnUpdate;
    String idNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.hire_me_test.R.layout.activity_edit_profile);

        idNumber = getIntent().getStringExtra("id_number");

        edtFullName = findViewById(com.example.hire_me_test.R.id.edtFullName);
        edtUsername = findViewById(com.example.hire_me_test.R.id.edtUsername);
        edtContact = findViewById(com.example.hire_me_test.R.id.edtContact);
        edtPermanent = findViewById(com.example.hire_me_test.R.id.edtPermanent);
        edtCurrent = findViewById(com.example.hire_me_test.R.id.edtCurrent);
        edtExperience = findViewById(com.example.hire_me_test.R.id.edtExperience);
        edtBankName = findViewById(com.example.hire_me_test.R.id.edtBankName);
        edtBranch = findViewById(com.example.hire_me_test.R.id.edtBranch);
        edtAccount = findViewById(com.example.hire_me_test.R.id.edtAccount);

        txtEmail = findViewById(com.example.hire_me_test.R.id.txtEmail);
        txtIdNumber = findViewById(com.example.hire_me_test.R.id.txtIdNumber);

        btnUpdate = findViewById(R.id.btnUpdateProfile);

        loadProfile();

        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void loadProfile() {
        String url = "https://hireme.cpsharetxt.com/get_worker_profile_12.php?id_number=" + idNumber;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("ProfileResponse", response);
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONObject user = obj.getJSONObject("user");
                            edtFullName.setText(user.optString("fullName", "N/A"));
                            edtUsername.setText(user.optString("username", "N/A"));
                            txtEmail.setText(user.optString("email", "N/A"));
                            txtIdNumber.setText(user.optString("idNumber", "N/A"));
                            edtContact.setText(user.optString("contactNumber", "N/A"));
                            edtPermanent.setText(user.optString("permanentAddress", "N/A"));
                            edtCurrent.setText(user.optString("currentAddress", "N/A"));
                            edtExperience.setText(user.optString("workExperience", "N/A"));
                            edtBankName.setText(user.optString("bankName", "N/A"));
                            edtBranch.setText(user.optString("bankBranch", "N/A"));
                            edtAccount.setText(user.optString("bankAccountNumber", "N/A"));
                        } else {
                            Toast.makeText(EditProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("JSONError", e.toString());
                        Toast.makeText(EditProfileActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(EditProfileActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void updateProfile() {
        String url = "https://hireme.cpsharetxt.com/update_worker_profile.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ViewProfileActivity.class);
                    intent.putExtra("id_number", idNumber);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("id_number", idNumber);
                param.put("fullName", edtFullName.getText().toString());
                param.put("username", edtUsername.getText().toString());
                param.put("contactNumber", edtContact.getText().toString());
                param.put("permanentAddress", edtPermanent.getText().toString());
                param.put("currentAddress", edtCurrent.getText().toString());
                param.put("workExperience", edtExperience.getText().toString());
                param.put("bankName", edtBankName.getText().toString());
                param.put("bankBranch", edtBranch.getText().toString());
                param.put("bankAccountNumber", edtAccount.getText().toString());
                return param;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
