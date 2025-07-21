package com.example.hire_me_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class GiveJobActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonNewUser;
    TextView textViewForgotPassword;

    private static final String LOGIN_URL = "https://hireme.cpsharetxt.com/login_employer.php";
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_job);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonNewUser = findViewById(R.id.buttonNewUser);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        buttonLogin.setOnClickListener(view -> {
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
