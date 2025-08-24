package com.example.hire_me_test.view.actvities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hire_me_test.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class EmployerProfileActivity extends AppCompatActivity {

    private EditText editCompanyName, editEmployerName, editAddress, editEmail, editContact;
    private ImageView companyIconImage;
    private Button btnUpdateEmployer, btnDeleteEmployer, btnSelectImage;
    private String employerId;
    private Uri selectedImageUri = null;

    private static final String BASE_URL = "https://hireme.cpsharetxt.com/";
    private static final int PICK_IMAGE_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);

        employerId = getIntent().getStringExtra("employer_id");

        // Toolbar with back button
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        companyIconImage = findViewById(R.id.companyIconImage);
        editCompanyName = findViewById(R.id.editCompanyName);
        editEmployerName = findViewById(R.id.editEmployerName);
        editAddress = findViewById(R.id.editAddress);
        editEmail = findViewById(R.id.editEmail);
        editContact = findViewById(R.id.editContact);
        btnUpdateEmployer = findViewById(R.id.btnUpdateEmployer);
        btnDeleteEmployer = findViewById(R.id.btnDeleteEmployer);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        loadEmployerDetails();

        btnSelectImage.setOnClickListener(v -> chooseImage());
        btnUpdateEmployer.setOnClickListener(v -> updateEmployer());
        btnDeleteEmployer.setOnClickListener(v -> confirmDelete());
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            companyIconImage.setImageURI(selectedImageUri); // Show selected image immediately
        }
    }

    private void loadEmployerDetails() {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "get_employer_details.php?id=" + employerId);
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) response.append(scanner.nextLine());
                scanner.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                if (jsonObject.getString("status").equals("success")) {
                    JSONObject employer = jsonObject.getJSONObject("employer");

                    runOnUiThread(() -> {
                        editCompanyName.setText(employer.optString("company_name"));
                        editEmployerName.setText(employer.optString("name"));
                        editAddress.setText(employer.optString("address"));
                        editEmail.setText(employer.optString("email"));
                        editContact.setText(employer.optString("contact"));

                        // Load company icon directly from the DB-stored URL
                        String iconUrl = employer.optString("company_icon");
                        if (iconUrl != null && !iconUrl.isEmpty()) {
                            Picasso.get()
                                    .load("https://hireme.cpsharetxt.com/" + iconUrl) // <-- direct link
                                    .placeholder(R.drawable.job)
                                    .error(R.drawable.job)
                                    .into(companyIconImage);
                        } else {
                            companyIconImage.setImageResource(R.drawable.job);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateEmployer() {
        if (employerId == null || employerId.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(this, "Missing Employer ID!", Toast.LENGTH_SHORT).show());
            return;
        }

        new Thread(() -> {
            try {
                String boundary = "===" + System.currentTimeMillis() + "===";
                URL url = new URL(BASE_URL + "update_employer.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());

                // Text fields
                writeFormField(out, "id", employerId, boundary);
                writeFormField(out, "company_name", editCompanyName.getText().toString(), boundary);
                writeFormField(out, "name", editEmployerName.getText().toString(), boundary);
                writeFormField(out, "address", editAddress.getText().toString(), boundary);
                writeFormField(out, "contact", editContact.getText().toString(), boundary);
                writeFormField(out, "email", editEmail.getText().toString(), boundary);

                // Image if selected
                if (selectedImageUri != null) {
                    File imageFile = getFileFromUri(selectedImageUri);
                    if (imageFile != null && imageFile.exists()) {
                        writeFileField(out, "company_icon", imageFile, boundary);
                    }
                }

                out.writeBytes("--" + boundary + "--\r\n");
                out.flush();
                out.close();

                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) response.append(scanner.nextLine());
                scanner.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                runOnUiThread(() -> Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void writeFormField(DataOutputStream out, String name, String value, String boundary) throws Exception {
        out.writeBytes("--" + boundary + "\r\n");
        out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
        out.writeBytes(value + "\r\n");
    }

    private void writeFileField(DataOutputStream out, String fieldName, File uploadFile, String boundary) throws Exception {
        out.writeBytes("--" + boundary + "\r\n");
        out.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + uploadFile.getName() + "\"\r\n");
        out.writeBytes("Content-Type: image/jpeg\r\n\r\n");
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        out.writeBytes("\r\n");
    }

    private File getFileFromUri(Uri uri) {
        try {
            String fileName = getFileName(uri);
            File tempFile = new File(getCacheDir(), fileName);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            Log.e("FileUtils", "Error: " + e.getMessage());
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEmployer())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteEmployer() {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "delete_employer.php?id=" + employerId);
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) response.append(scanner.nextLine());
                scanner.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                runOnUiThread(() -> {
                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    if (jsonObject.optString("status").equals("success")) {
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
