package com.example.hire_me_test.view.actvities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hire_me_test.R;

import java.io.IOException;
import java.io.InputStream;
import okhttp3.*;

public class CompanyRegisterActivity extends AppCompatActivity {

    EditText companyName, name, address, email, contact, password, confirmPassword;
    TextView selectedFile;
    Button uploadIcon, createAccountBtn, signInBtn;
    CheckBox policyCheckbox;
    Uri iconUri;
    ImageView btnBack;

    private static final int PICK_ICON_REQUEST = 1;
    private static final String UPLOAD_URL = "https://hireme.cpsharetxt.com/register_employer.php"; // Change to your real path

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register);

        companyName = findViewById(R.id.companyName);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        policyCheckbox = findViewById(R.id.policyCheckbox);
        uploadIcon = findViewById(R.id.uploadIcon);
        selectedFile = findViewById(R.id.selectedFile);
        createAccountBtn = findViewById(R.id.createAccountBtn);
        signInBtn = findViewById(R.id.signInBtn);

        uploadIcon.setOnClickListener(v -> openFileChooser());

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        signInBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CompanyRegisterActivity.this, GiveJobActivity.class);
            startActivity(intent);
        });

        createAccountBtn.setOnClickListener(v -> {
            if (!policyCheckbox.isChecked()) {
                Toast.makeText(this, "You must agree to the Privacy Policy", Toast.LENGTH_SHORT).show();
                return;
            }
            showConfirmationDialog();
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Your Information");
        builder.setMessage("Please confirm that all information you provided is correct.\n\n" +
                "We do not guarantee any claims or outcomes based on this application.");
        builder.setCancelable(false);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            dialog.dismiss();
            uploadData();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            Toast.makeText(this, "Please review your information.", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Company Icon"), PICK_ICON_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_ICON_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            iconUri = data.getData();
            selectedFile.setText(getFileName(iconUri));
        }
    }

    private String getFileName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) return null;
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String fileName = cursor.getString(nameIndex);
        cursor.close();
        return fileName;
    }

    private void uploadData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            OkHttpClient client = new OkHttpClient();

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("company_name", companyName.getText().toString())
                    .addFormDataPart("name", name.getText().toString())
                    .addFormDataPart("address", address.getText().toString())
                    .addFormDataPart("email", email.getText().toString())
                    .addFormDataPart("contact", contact.getText().toString())
                    .addFormDataPart("password", password.getText().toString())
                    .addFormDataPart("confirm_password", confirmPassword.getText().toString());

            if (iconUri != null) {
                InputStream inputStream = getContentResolver().openInputStream(iconUri);
                byte[] fileBytes = new byte[inputStream.available()];
                inputStream.read(fileBytes);
                String fileName = getFileName(iconUri);
                builder.addFormDataPart("company_icon", fileName,
                        RequestBody.create(fileBytes, MediaType.parse(getContentResolver().getType(iconUri))));
            }

            RequestBody requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(CompanyRegisterActivity.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    progressDialog.dismiss();
                    final String res = response.body().string();
                    runOnUiThread(() -> {
                        if (res.contains("\"success\":true")) {
                            Toast.makeText(CompanyRegisterActivity.this, "Account created successfully!", Toast.LENGTH_LONG).show();
                            clearFields();
                        } else {
                            Toast.makeText(CompanyRegisterActivity.this, "Registration failed: " + res, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        companyName.setText("");
        name.setText("");
        address.setText("");
        email.setText("");
        contact.setText("");
        password.setText("");
        confirmPassword.setText("");
        selectedFile.setText("No file selected");
        policyCheckbox.setChecked(false);
        iconUri = null;
    }
}
