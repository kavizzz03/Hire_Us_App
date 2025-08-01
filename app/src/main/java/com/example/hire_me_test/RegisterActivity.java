package com.example.hire_me_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, username, contactNumber, email, idNumber, permanentAddress, currentAddress,
            workExperience, password, confirmPassword, bankAccountNumber, bankBranch, customJobTitle;
    Spinner jobTitleSpinner, bankNameSpinner;
    LinearLayout customJobTitleLayout;
    ImageView idFrontImage, idBackImage;
    Button uploadIdFrontBtn, uploadIdBackBtn, createAccountBtn, signInBtn;
    CheckBox privacyPolicyCheckbox;

    Bitmap bitmapFront, bitmapBack;
    final int REQ_FRONT = 100, REQ_BACK = 200;

    // Job Titles including "Other"
    List<String> jobTitles = Arrays.asList(
            "Select Job Title",
            "Welder",
            "Labour/Helper",
            "Electrician",
            "Mason",
            "Painter",
            "Aluminium Fitter",
            "Technician",
            "Other (Fill Custom Title Field)"
    );

    // More complete Sri Lankan bank list
    List<String> banks = Arrays.asList(
            "Select Bank Name",
            "People's Bank",
            "Sampath Bank",
            "Commercial Bank",
            "Hatton National Bank",
            "National Savings Bank",
            "Bank of Ceylon",
            "Nations Trust Bank",
            "DFCC Bank",
            "Pan Asia Bank",
            "Amana Bank",
            "Seylan Bank",
            "Union Bank",
            "State Bank of India",
            "Indian Bank",
            "Axis Bank",
            "Cargills Bank",
            "HNB Grameen",
            "Public Bank",
            "Standard Chartered Bank"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        signInBtn = findViewById(R.id.signInBtn);
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
        bankAccountNumber = findViewById(R.id.bankAccountNumber);
        bankBranch = findViewById(R.id.bankBranch);
        customJobTitle = findViewById(R.id.customJobTitle);

        jobTitleSpinner = findViewById(R.id.jobTitleSpinner);
        bankNameSpinner = findViewById(R.id.bankNameSpinner);
        customJobTitleLayout = findViewById(R.id.customJobTitleLayout);

        idFrontImage = findViewById(R.id.idFrontImage);
        idBackImage = findViewById(R.id.idBackImage);
        uploadIdFrontBtn = findViewById(R.id.uploadIdFrontBtn);
        uploadIdBackBtn = findViewById(R.id.uploadIdBackBtn);
        createAccountBtn = findViewById(R.id.createAccountBtn);
        privacyPolicyCheckbox = findViewById(R.id.privacyPolicyCheckbox);

        // Setup spinners
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTitles);
        jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobTitleSpinner.setAdapter(jobAdapter);

        ArrayAdapter<String> bankAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, banks);
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankNameSpinner.setAdapter(bankAdapter);

        // Show/hide custom job title field if "Other" is selected
        jobTitleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = jobTitles.get(position);
                if (selected.equals("Other (Fill Custom Title Field)")) {
                    customJobTitleLayout.setVisibility(View.VISIBLE);
                } else {
                    customJobTitleLayout.setVisibility(View.GONE);
                    customJobTitle.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                customJobTitleLayout.setVisibility(View.GONE);
                customJobTitle.setText("");
            }
        });

        signInBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, FindJobActivity.class));
            finish();
        });

        uploadIdFrontBtn.setOnClickListener(v -> pickImage(REQ_FRONT));
        uploadIdBackBtn.setOnClickListener(v -> pickImage(REQ_BACK));

        createAccountBtn.setOnClickListener(v -> {
            if (!areAllFieldsFilled()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (jobTitleSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select a Job Title", Toast.LENGTH_SHORT).show();
                return;
            }

            if (jobTitleSpinner.getSelectedItem().toString().equals("Other (Fill Custom Title Field)") &&
                    customJobTitle.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter a custom job title", Toast.LENGTH_SHORT).show();
                return;
            }

            if (bankNameSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select a Bank Name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!privacyPolicyCheckbox.isChecked()) {
                Toast.makeText(this, "You must agree to the Privacy Policy", Toast.LENGTH_SHORT).show();
                return;
            }

            showPrivacyPolicyDialog();
        });
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (requestCode == REQ_FRONT) {
                    bitmapFront = selectedImage;
                    idFrontImage.setImageBitmap(bitmapFront);
                } else if (requestCode == REQ_BACK) {
                    bitmapBack = selectedImage;
                    idBackImage.setImageBitmap(bitmapBack);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPrivacyPolicyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Privacy Policy Agreement")
                .setMessage("By creating an account, you agree to our Privacy Policy regarding the storage and use of your personal and sensitive data.")
                .setCancelable(false)
                .setPositiveButton("I Agree", (dialog, which) -> registerUser())
                .setNegativeButton("Cancel", (dialog, which) ->
                        Toast.makeText(this, "You must accept the Privacy Policy to create an account.", Toast.LENGTH_SHORT).show())
                .show();
    }

    private boolean areAllFieldsFilled() {
        boolean basicFields = !fullName.getText().toString().isEmpty()
                && !username.getText().toString().isEmpty()
                && !contactNumber.getText().toString().isEmpty()
                && !email.getText().toString().isEmpty()
                && !idNumber.getText().toString().isEmpty()
                && !permanentAddress.getText().toString().isEmpty()
                && !currentAddress.getText().toString().isEmpty()
                && !workExperience.getText().toString().isEmpty()
                && !password.getText().toString().isEmpty()
                && !confirmPassword.getText().toString().isEmpty()
                && !bankAccountNumber.getText().toString().isEmpty()
                && !bankBranch.getText().toString().isEmpty()
                && bitmapFront != null
                && bitmapBack != null;

        boolean customJobFilled = true;
        if (jobTitleSpinner.getSelectedItem() != null &&
                jobTitleSpinner.getSelectedItem().toString().equals("Other (Fill Custom Title Field)")) {
            customJobFilled = !customJobTitle.getText().toString().trim().isEmpty();
        }

        return basicFields && customJobFilled;
    }

    private void registerUser() {
        String url = "https://hireme.cpsharetxt.com/register_worker.php";

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Account Created!", Toast.LENGTH_LONG).show();
                    clearAllFields();
                    startActivity(new Intent(RegisterActivity.this, FindJobActivity.class));
                    finish();
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("fullName", fullName.getText().toString());
                params.put("username", username.getText().toString());
                params.put("contactNumber", contactNumber.getText().toString());
                params.put("email", email.getText().toString());
                params.put("idNumber", idNumber.getText().toString());
                params.put("permanentAddress", permanentAddress.getText().toString());
                params.put("currentAddress", currentAddress.getText().toString());
                params.put("workExperience", workExperience.getText().toString());
                params.put("password", password.getText().toString());
                params.put("bankAccountNumber", bankAccountNumber.getText().toString());
                params.put("bankBranch", bankBranch.getText().toString());

                // Job Title handling
                String jobTitle = jobTitleSpinner.getSelectedItem().toString();
                if (jobTitle.equals("Other (Fill Custom Title Field)")) {
                    jobTitle = customJobTitle.getText().toString().trim();
                }
                params.put("jobTitle", jobTitle);

                // Bank Name
                String bankName = bankNameSpinner.getSelectedItem().toString();
                params.put("bankName", bankName);

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> dataPartMap = new HashMap<>();
                if (bitmapFront != null) {
                    dataPartMap.put("idFront", new DataPart("front.jpg", getFileDataFromBitmap(bitmapFront)));
                }
                if (bitmapBack != null) {
                    dataPartMap.put("idBack", new DataPart("back.jpg", getFileDataFromBitmap(bitmapBack)));
                }
                return dataPartMap;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private byte[] getFileDataFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        return bos.toByteArray();
    }

    private void clearAllFields() {
        fullName.setText("");
        username.setText("");
        contactNumber.setText("");
        email.setText("");
        idNumber.setText("");
        permanentAddress.setText("");
        currentAddress.setText("");
        workExperience.setText("");
        password.setText("");
        confirmPassword.setText("");
        bankAccountNumber.setText("");
        bankBranch.setText("");
        customJobTitle.setText("");
        jobTitleSpinner.setSelection(0);
        bankNameSpinner.setSelection(0);
        idFrontImage.setImageResource(R.mipmap.icon1);
        idBackImage.setImageResource(R.mipmap.icon1);
        bitmapFront = null;
        bitmapBack = null;
        privacyPolicyCheckbox.setChecked(false);
    }
}
