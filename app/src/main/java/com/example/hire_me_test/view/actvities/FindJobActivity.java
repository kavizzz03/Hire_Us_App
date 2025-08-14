package com.example.hire_me_test.view.actvities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FindJobActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonNewUser;
    private TextView textViewForgotPassword;
    private ImageButton btnTogglePassword;
    private CheckBox checkBoxRemember;

    private boolean isPasswordVisible = false;

    private static final String LOGIN_URL = "https://hireme.cpsharetxt.com/login.php";

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "HireMePrefs";
    private static final String KEY_ID_NUMBER = "id_number";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        checkBoxRemember = findViewById(R.id.checkBoxRemember);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonNewUser = findViewById(R.id.buttonNewUser);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        loadSavedCredentials();

        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            // Reset typeface to avoid monospace weirdness
            editTextPassword.setTypeface(Typeface.DEFAULT);
            editTextPassword.setSelection(editTextPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        buttonLogin.setOnClickListener(v -> {
            String idNumber = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (idNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter ID number and password", Toast.LENGTH_SHORT).show();
                return;
            }

            saveCredentials(idNumber, password);

            checkLogin(idNumber, password);
        });

        buttonNewUser.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        textViewForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ResetRequestActivity.class)));
    }

    private void loadSavedCredentials() {
        boolean remember = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        if (remember) {
            editTextEmail.setText(sharedPreferences.getString(KEY_ID_NUMBER, ""));
            editTextPassword.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            checkBoxRemember.setChecked(true);
        }
    }

    private void saveCredentials(String idNumber, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (checkBoxRemember.isChecked()) {
            editor.putString(KEY_ID_NUMBER, idNumber);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER, true);
        } else {
            editor.clear();
        }
        editor.apply();
    }

    private void checkLogin(String idNumber, String password) {
        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equals("success")) {
                            String fullName = jsonObject.getString("full_name");
                            String jobTitle = jsonObject.getString("job_title");

                            Intent intent = new Intent(this, EmpProfileActivity.class);
                            intent.putExtra("id_number", idNumber);
                            intent.putExtra("full_name", fullName);
                            intent.putExtra("job_title", jobTitle);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Response parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_number", idNumber);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
