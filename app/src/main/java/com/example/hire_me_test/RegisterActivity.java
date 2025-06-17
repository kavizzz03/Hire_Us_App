package com.example.hire_me_test;

import android.app.Activity;
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
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, username, contactNumber, email, idNumber, permanentAddress, currentAddress,
            workExperience, password, confirmPassword, bankAccountNumber, bankName, bankBranch;
    ImageView idFrontImage, idBackImage;
    Button uploadIdFrontBtn, uploadIdBackBtn, createAccountBtn;

    Bitmap bitmapFront, bitmapBack;
    final int REQ_FRONT = 100, REQ_BACK = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        bankName = findViewById(R.id.bankName);
        bankBranch = findViewById(R.id.bankBranch);

        idFrontImage = findViewById(R.id.idFrontImage);
        idBackImage = findViewById(R.id.idBackImage);
        uploadIdFrontBtn = findViewById(R.id.uploadIdFrontBtn);
        uploadIdBackBtn = findViewById(R.id.uploadIdBackBtn);
        createAccountBtn = findViewById(R.id.createAccountBtn);

        uploadIdFrontBtn.setOnClickListener(v -> pickImage(REQ_FRONT));
        uploadIdBackBtn.setOnClickListener(v -> pickImage(REQ_BACK));

        createAccountBtn.setOnClickListener(v -> {
            if (!areAllFieldsFilled()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                registerUser();
            }
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
            }
        }
    }

    private void registerUser() {
        String url = "https://hireme.cpsharetxt.com/register_worker.php"; // your actual PHP file

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Account Created!", Toast.LENGTH_LONG).show();
                    clearAllFields();
                    startActivity(new Intent(RegisterActivity.this, FindJobActivity.class));
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (bitmapFront != null)
                    params.put("idFront", new DataPart("front.jpg", getFileDataFromBitmap(bitmapFront)));
                if (bitmapBack != null)
                    params.put("idBack", new DataPart("back.jpg", getFileDataFromBitmap(bitmapBack)));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("fullName", fullName.getText().toString());
                map.put("username", username.getText().toString());
                map.put("contactNumber", contactNumber.getText().toString());
                map.put("email", email.getText().toString());
                map.put("idNumber", idNumber.getText().toString());
                map.put("permanentAddress", permanentAddress.getText().toString());
                map.put("currentAddress", currentAddress.getText().toString());
                map.put("workExperience", workExperience.getText().toString());
                map.put("password", password.getText().toString());
                map.put("bankAccountNumber", bankAccountNumber.getText().toString());
                map.put("bankName", bankName.getText().toString());
                map.put("bankBranch", bankBranch.getText().toString());
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private byte[] getFileDataFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        return bos.toByteArray();
    }

    private boolean areAllFieldsFilled() {
        return !fullName.getText().toString().isEmpty() &&
                !username.getText().toString().isEmpty() &&
                !contactNumber.getText().toString().isEmpty() &&
                !email.getText().toString().isEmpty() &&
                !idNumber.getText().toString().isEmpty() &&
                !permanentAddress.getText().toString().isEmpty() &&
                !currentAddress.getText().toString().isEmpty() &&
                !workExperience.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() &&
                !confirmPassword.getText().toString().isEmpty() &&
                !bankAccountNumber.getText().toString().isEmpty() &&
                !bankName.getText().toString().isEmpty() &&
                !bankBranch.getText().toString().isEmpty() &&
                bitmapFront != null &&
                bitmapBack != null;
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
        bankName.setText("");
        bankBranch.setText("");
        idFrontImage.setImageResource(R.mipmap.icon1); // Replace with your placeholder image
        idBackImage.setImageResource(R.mipmap.icon1);  // Replace with your placeholder image
        bitmapFront = null;
        bitmapBack = null;
    }
}
