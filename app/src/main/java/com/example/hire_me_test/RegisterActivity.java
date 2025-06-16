package com.example.hire_me_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    // Fields
    EditText fullName, username, contactNumber, email, idNumber,
            permanentAddress, currentAddress, workExperience,
            password, confirmPassword, bankAccountNumber, bankName, bankBranch;

    ImageView idFrontImage, idBackImage;
    Button uploadIdFrontBtn, uploadIdBackBtn, createAccountBtn, signInBtn;

    private static final int PICK_FRONT_IMAGE = 100;
    private static final int PICK_BACK_IMAGE = 101;

    private Uri frontImageUri, backImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Bind views
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
        signInBtn = findViewById(R.id.signInBtn);

        // Upload ID Front
        uploadIdFrontBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_FRONT_IMAGE);
        });

        // Upload ID Back
        uploadIdBackBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_BACK_IMAGE);
        });

        // Create account
        createAccountBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                // TODO: Send data and images to PHP backend here
            }
        });

        // Go to sign in
        signInBtn.setOnClickListener(v -> {
            // TODO: startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                if (requestCode == PICK_FRONT_IMAGE) {
                    frontImageUri = selectedImage;
                    idFrontImage.setImageBitmap(bitmap);
                } else if (requestCode == PICK_BACK_IMAGE) {
                    backImageUri = selectedImage;
                    idBackImage.setImageBitmap(bitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(fullName.getText()) ||
                TextUtils.isEmpty(username.getText()) ||
                TextUtils.isEmpty(contactNumber.getText()) ||
                TextUtils.isEmpty(email.getText()) ||
                TextUtils.isEmpty(idNumber.getText()) ||
                TextUtils.isEmpty(permanentAddress.getText()) ||
                TextUtils.isEmpty(password.getText()) ||
                TextUtils.isEmpty(confirmPassword.getText()) ||
                TextUtils.isEmpty(bankAccountNumber.getText()) ||
                TextUtils.isEmpty(bankName.getText()) ||
                TextUtils.isEmpty(bankBranch.getText()) ||
                frontImageUri == null ||
                backImageUri == null) {

            Toast.makeText(this, "Please fill all required fields and select ID images.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
