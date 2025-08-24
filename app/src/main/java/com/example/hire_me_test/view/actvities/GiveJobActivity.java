package com.example.hire_me_test.view.actvities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hire_me_test.R;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class GiveJobActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonNewUser;
    TextView textViewForgotPassword;
    ImageButton btnTogglePassword;
    CheckBox checkBoxRemember;

    boolean isPasswordVisible = false;

    private static final String LOGIN_URL = "https://hireme.cpsharetxt.com/login_employer.php";
    OkHttpClient client = new OkHttpClient();

    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "HireMePrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_job);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        checkBoxRemember = findViewById(R.id.checkBoxRemember);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonNewUser = findViewById(R.id.buttonNewUser);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Check network connection on activity load
        if (!isNetworkConnected()) {
            Toast.makeText(this, "No internet connection. Please connect and try again.", Toast.LENGTH_LONG).show();
        }

        // Load saved credentials if Remember Me was checked
        loadSavedCredentials();

        // Toggle password visibility
        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            editTextPassword.setSelection(editTextPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // Login button click
        buttonLogin.setOnClickListener(view -> {
            if (!isNetworkConnected()) {
                Toast.makeText(this, "No internet connection. Please connect and try again.", Toast.LENGTH_LONG).show();
                return;
            }

            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        buttonNewUser.setOnClickListener(view -> {
            startActivity(new Intent(GiveJobActivity.this, CompanyRegisterActivity.class));
        });

        textViewForgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(GiveJobActivity.this, ForgotPasswordActivity.class));
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void loadSavedCredentials() {
        boolean remember = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        if (remember) {
            editTextEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));
            editTextPassword.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            checkBoxRemember.setChecked(true);
        }
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (checkBoxRemember.isChecked()) {
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER, true);
        } else {
            editor.clear(); // Clear saved credentials if unchecked
        }
        editor.apply();
    }

    private void loginUser(String email, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(GiveJobActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    boolean success = json.getBoolean("success");
                    String message = json.getString("message");

                    runOnUiThread(() -> {
                        if (success) {
                            saveCredentials(email, password); // Save if Remember Me is checked

                            String userId = json.optString("user_id");
                            String companyName = json.optString("company_name");

                            Intent intent = new Intent(GiveJobActivity.this, DashboardActivity.class);
                            intent.putExtra("user_id", userId);
                            intent.putExtra("company_name", companyName);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(GiveJobActivity.this, "Login failed: " + message, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(GiveJobActivity.this, "Error parsing server response", Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }
}
