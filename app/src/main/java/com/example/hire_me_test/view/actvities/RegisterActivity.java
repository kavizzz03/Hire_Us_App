package com.example.hire_me_test.view.actvities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.network.VolleyMultipartRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, username, contactNumber, email, idNumber, permanentAddress, currentAddress,
            workExperience, password, confirmPassword, bankAccountNumber, bankBranch, customJobTitle;
    Spinner jobTitleSpinner, bankNameSpinner;
    LinearLayout customJobTitleLayout;
    ImageView idFrontImage, idBackImage;
    Button createAccountBtn, signInBtn;
    Button cameraFrontBtn, cameraBackBtn, galleryFrontBtn, galleryBackBtn;
    CheckBox privacyPolicyCheckbox;

    Bitmap bitmapFront, bitmapBack;

    private static final int REQ_FRONT = 100;
    private static final int REQ_BACK = 200;
    private static final int CAMERA_PERMISSION_CODE = 300;

    List<String> jobTitles = Arrays.asList(
            "Select Job Title","Welder","Labour/Helper","Electrician","Mason","Painter",
            "Aluminium Fitter","Technician","Other (Fill Custom Title Field)"
    );

    List<String> banks = Arrays.asList(
            "Select Bank Name","People's Bank","Sampath Bank","Commercial Bank","Hatton National Bank",
            "National Savings Bank","Bank of Ceylon","Nations Trust Bank","DFCC Bank","Pan Asia Bank",
            "Amana Bank","Seylan Bank","Union Bank","State Bank of India","Indian Bank","Axis Bank",
            "Cargills Bank","HNB Grameen","Public Bank","Standard Chartered Bank"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        checkPermissions();

        initViews();
        setupSpinners();
        setupListeners();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.CAMERA);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (!permissions.isEmpty())
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_CODE){
            boolean granted = true;
            for(int res : grantResults){
                if(res != PackageManager.PERMISSION_GRANTED) granted = false;
            }
            if(!granted) Toast.makeText(this,"Camera & Storage permission required!",Toast.LENGTH_LONG).show();
        }
    }

    private void initViews(){
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

        cameraFrontBtn = findViewById(R.id.cameraFrontBtn);
        cameraBackBtn = findViewById(R.id.cameraBackBtn);
        galleryFrontBtn = findViewById(R.id.galleryFrontBtn);
        galleryBackBtn = findViewById(R.id.galleryBackBtn);

        createAccountBtn = findViewById(R.id.createAccountBtn);
        privacyPolicyCheckbox = findViewById(R.id.privacyPolicyCheckbox);
    }

    private void setupSpinners(){
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTitles);
        jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobTitleSpinner.setAdapter(jobAdapter);

        ArrayAdapter<String> bankAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, banks);
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankNameSpinner.setAdapter(bankAdapter);

        jobTitleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = jobTitles.get(position);
                customJobTitleLayout.setVisibility(selected.equals("Other (Fill Custom Title Field)") ? View.VISIBLE : View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { customJobTitleLayout.setVisibility(View.GONE); }
        });
    }

    private void setupListeners(){
        signInBtn.setOnClickListener(v -> finish());

        cameraFrontBtn.setOnClickListener(v -> openCamera(REQ_FRONT));
        cameraBackBtn.setOnClickListener(v -> openCamera(REQ_BACK));
        galleryFrontBtn.setOnClickListener(v -> pickImageFromGallery(REQ_FRONT));
        galleryBackBtn.setOnClickListener(v -> pickImageFromGallery(REQ_BACK));

        createAccountBtn.setOnClickListener(v -> {
            if(!areAllFieldsFilled()) { showToast("Please fill all required fields"); return; }
            if(!password.getText().toString().equals(confirmPassword.getText().toString())) { showToast("Passwords do not match"); return; }
            if(jobTitleSpinner.getSelectedItemPosition() == 0) { showToast("Select Job Title"); return; }
            if(jobTitleSpinner.getSelectedItem().toString().equals("Other (Fill Custom Title Field)") &&
                    customJobTitle.getText().toString().trim().isEmpty()) { showToast("Enter custom job title"); return; }
            if(bankNameSpinner.getSelectedItemPosition() == 0) { showToast("Select Bank"); return; }
            if(!privacyPolicyCheckbox.isChecked()) { showToast("Accept Privacy Policy"); return; }

            showPrivacyPolicyDialog();
        });
    }

    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void openCamera(int requestCode){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, requestCode);
    }

    private void pickImageFromGallery(int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            try {
                Bitmap bitmap = null;
                if(data.getData()!=null) { // gallery
                    Uri uri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } else if(data.getExtras()!=null) { // camera
                    bitmap = (Bitmap) data.getExtras().get("data");
                }

                if(requestCode == REQ_FRONT) { bitmapFront = bitmap; idFrontImage.setImageBitmap(bitmapFront);}
                else if(requestCode == REQ_BACK) { bitmapBack = bitmap; idBackImage.setImageBitmap(bitmapBack);}
            } catch (IOException e){
                e.printStackTrace();
                showToast("Failed to load image");
            }
        }
    }

    private boolean areAllFieldsFilled(){
        boolean basic = !fullName.getText().toString().isEmpty() &&
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
                !bankBranch.getText().toString().isEmpty() &&
                bitmapFront!=null && bitmapBack!=null;

        if(jobTitleSpinner.getSelectedItem().toString().equals("Other (Fill Custom Title Field)"))
            return basic && !customJobTitle.getText().toString().trim().isEmpty();

        return basic;
    }

    private void showPrivacyPolicyDialog(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("Privacy Policy Agreement")
                .setMessage("By creating an account, you agree to our Privacy Policy regarding storage and use of your personal data.")
                .setCancelable(false)
                .setPositiveButton("I Agree", (dialog, which) -> registerUser())
                .setNegativeButton("Cancel", (dialog, which) -> showToast("You must accept Privacy Policy"))
                .show();
    }

    private void registerUser(){
        String url = "https://hireme.cpsharetxt.com/register_worker.php";

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, url,
                response -> showSuccessDialog(),
                error -> showToast("Error: "+error.getMessage())) {

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
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

                String jobTitle = jobTitleSpinner.getSelectedItem().toString();
                if(jobTitle.equals("Other (Fill Custom Title Field)"))
                    jobTitle = customJobTitle.getText().toString().trim();
                params.put("jobTitle",jobTitle);
                params.put("bankName",bankNameSpinner.getSelectedItem().toString());

                return params;
            }

            @Override
            protected Map<String,DataPart> getByteData(){
                Map<String,DataPart> dataPartMap = new HashMap<>();
                if(bitmapFront!=null) dataPartMap.put("idFront",new DataPart("front.jpg",getFileDataFromBitmap(bitmapFront)));
                if(bitmapBack!=null) dataPartMap.put("idBack",new DataPart("back.jpg",getFileDataFromBitmap(bitmapBack)));
                return dataPartMap;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void showSuccessDialog(){
        new MaterialAlertDialogBuilder(this)
                .setTitle("🎉 Account Created!")
                .setMessage("Welcome "+fullName.getText().toString()+"!\nYour account has been successfully created. Start applying for jobs now!")
                .setPositiveButton("Continue", (dialog, which) -> {
                    clearAllFields();
                    startActivity(new Intent(RegisterActivity.this, FindJobActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private byte[] getFileDataFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,bos);
        return bos.toByteArray();
    }

    private void clearAllFields(){
        fullName.setText(""); username.setText(""); contactNumber.setText(""); email.setText("");
        idNumber.setText(""); permanentAddress.setText(""); currentAddress.setText(""); workExperience.setText("");
        password.setText(""); confirmPassword.setText(""); bankAccountNumber.setText(""); bankBranch.setText(""); customJobTitle.setText("");
        bitmapFront=null; bitmapBack=null;
        idFrontImage.setImageResource(android.R.drawable.ic_menu_camera);
        idBackImage.setImageResource(android.R.drawable.ic_menu_camera);
        jobTitleSpinner.setSelection(0); bankNameSpinner.setSelection(0);
        privacyPolicyCheckbox.setChecked(false);
    }
}
