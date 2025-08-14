package com.example.hire_me_test.view.actvities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.hire_me_test.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetRequestActivity extends AppCompatActivity {

    private static final String TAG = "ResetRequestActivity";

    EditText editTextID, editTextEmail;
    Button buttonSendLink;

    private static final String RESET_URL = "https://hireme.cpsharetxt.com/send_reset_link.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.hire_me_test.R.layout.activity_reset_request);

        editTextID = findViewById(com.example.hire_me_test.R.id.editTextID);
        editTextEmail = findViewById(com.example.hire_me_test.R.id.editTextEmail);
        buttonSendLink = findViewById(R.id.buttonSendLink);



        buttonSendLink.setOnClickListener(v -> {
            String id = editTextID.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();

            Log.d(TAG, "Button clicked with ID: " + id + " and Email: " + email);

            if (id.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please enter both ID and Email", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Input validation failed");
                return;
            }

            buttonSendLink.setEnabled(false);

            StringRequest request = new StringRequest(Request.Method.POST, RESET_URL,
                    response -> {
                        buttonSendLink.setEnabled(true);
                        Log.d(TAG, "Response received: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.optString("status");
                            String message = jsonObject.optString("message");

                            if ("success".equalsIgnoreCase(status)) {
                                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                                // Clear fields
                                editTextID.setText("");
                                editTextEmail.setText("");

                                // Navigate to FindJobActivity
                                Intent intent = new Intent(ResetRequestActivity.this, FindJobActivity.class);
                                startActivity(intent);
                                finish(); // Optional: close this activity
                            } else {
                                Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Response parsing error", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "JSON Exception", e);
                        }
                    },
                    error -> {
                        buttonSendLink.setEnabled(true);
                        String errMsg = "Network error";
                        if (error.getMessage() != null) {
                            errMsg += ": " + error.getMessage();
                        }
                        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Volley error", error);
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_number", id);
                    params.put("email", email);
                    Log.d(TAG, "Parameters sent: " + params.toString());
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
    }
}
