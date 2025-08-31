package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;

import org.json.*;

public class ViewProfileActivity extends AppCompatActivity {

    TextView txtName, txtUsername, txtEmail, txtIdNumber, txtContact, txtPermanent, txtCurrent, txtExperience, txtBankName, txtBranch, txtAccount;
    Button btnEdit, btnDelete;
    String idNumber;

    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        idNumber = getIntent().getStringExtra("id_number");
        if (idNumber == null || idNumber.isEmpty()) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtName = findViewById(R.id.txtFullName);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtIdNumber = findViewById(R.id.txtIdNumber);
        txtContact = findViewById(R.id.txtContact);
        txtPermanent = findViewById(R.id.txtPermanent);
        txtCurrent = findViewById(R.id.txtCurrent);
        txtExperience = findViewById(R.id.txtExperience);
        txtBankName = findViewById(R.id.txtBankName);
        txtBranch = findViewById(R.id.txtBranch);
        txtAccount = findViewById(R.id.txtAccount);
        btnEdit = findViewById(R.id.btnEditProfile);
        btnDelete = findViewById(R.id.btnDeleteProfile);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        loadProfile();

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("id_number", idNumber);
            startActivity(intent);
        });

       btnDelete.setOnClickListener(v -> new AlertDialog.Builder(ViewProfileActivity.this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete your profile?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProfile())
                .setNegativeButton("No", null)
                .show());
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
                            txtName.setText(user.optString("fullName", "N/A"));
                            txtUsername.setText(user.optString("username", "N/A"));
                            txtEmail.setText(user.optString("email", "N/A"));
                            txtIdNumber.setText(user.optString("idNumber", "N/A"));
                            txtContact.setText(user.optString("contactNumber", "N/A"));
                            txtPermanent.setText(user.optString("permanentAddress", "N/A"));
                            txtCurrent.setText(user.optString("currentAddress", "N/A"));
                            txtExperience.setText(user.optString("workExperience", "N/A"));
                            txtBankName.setText(user.optString("bankName", "N/A"));
                            txtBranch.setText(user.optString("bankBranch", "N/A"));
                            txtAccount.setText(user.optString("bankAccountNumber", "N/A"));
                        } else {
                            Toast.makeText(ViewProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("JSONError", e.toString());
                        Toast.makeText(ViewProfileActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(ViewProfileActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void deleteProfile() {
        // Use correct param name as per your PHP code: "idNumber" or "id_number"
        // Here I keep "id_number" because your PHP example used that
        String urlWithId = "https://hireme.cpsharetxt.com/delete_worker_profile.php?id_number=" + idNumber;

        Log.d("DELETE_PROFILE_REQUEST", "URL: " + urlWithId);

        StringRequest request = new StringRequest(Request.Method.GET, urlWithId,
                response -> {
                    Log.d("DELETE_PROFILE_RESPONSE", "Raw response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.optBoolean("success", false);
                        String message = jsonObject.optString("message", "No message from server");

                        int ratingsDeleted = jsonObject.optInt("ratings_deleted", 0);
                        String fullMessage = message + (ratingsDeleted > 0 ? " (" + ratingsDeleted + " ratings deleted)" : "");

                        Toast.makeText(ViewProfileActivity.this, fullMessage, Toast.LENGTH_LONG).show();

                        if (success) {
                            Intent intent = new Intent(ViewProfileActivity.this, FindJobActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                    } catch (JSONException e) {
                        Log.e("JSON_PARSE_ERROR", "Failed to parse JSON: " + e.getMessage());
                        Log.e("JSON_PARSE_ERROR", "Raw server response: " + response);
                        e.printStackTrace();
                        Toast.makeText(ViewProfileActivity.this, "Error parsing server response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String errorMsg = "Network error";
                    if (error.networkResponse != null) {
                        errorMsg += " | Code: " + error.networkResponse.statusCode;
                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            Log.e("DELETE_PROFILE_ERROR", "Server response: " + responseBody);
                        } catch (Exception e) {
                            Log.e("DELETE_PROFILE_ERROR", "Failed to read error response");
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("DELETE_PROFILE_ERROR", "No network response");
                    }

                    error.printStackTrace();
                    Toast.makeText(ViewProfileActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });

        // Optional: Set a retry policy for network request timeouts and retries
        request.setRetryPolicy(new DefaultRetryPolicy(
                8000, // 8 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }





}
