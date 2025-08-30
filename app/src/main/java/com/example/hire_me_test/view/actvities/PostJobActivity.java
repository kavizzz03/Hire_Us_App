package com.example.hire_me_test.view.actvities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.example.hire_me_test.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostJobActivity extends AppCompatActivity {

    Spinner spinnerJobTitle;
    EditText editCustomTitle, editVacancies, editStartTime, editEndTime,
            editLocation, editSalary, editOtSalary, editRequirements,
            editDate, editPickup, editContact;
    Button btnPostJob;
    ImageButton btnDatePicker, btnBack;

    String[] jobTitles = {
            "Select the Job Title","Welder", "Labour/Helper", "Electrician", "Meason", "Painter",
            "Aluminium Fitter", "Technicien", "Driver", "Other (Fill Custom Title Field)"
    };

    String postJobUrl = "https://hireme.cpsharetxt.com/post_job.php";
    String employeeId;  // will get from Intent extras

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.hire_me_test.R.layout.activity_post_job);

        // Initialize views
        spinnerJobTitle = findViewById(com.example.hire_me_test.R.id.spinnerJobTitle);
        editCustomTitle = findViewById(com.example.hire_me_test.R.id.editCustomTitle);
        editVacancies = findViewById(com.example.hire_me_test.R.id.editVacancies);
        editStartTime = findViewById(com.example.hire_me_test.R.id.editStartTime);
        editEndTime = findViewById(com.example.hire_me_test.R.id.editEndTime);
        editLocation = findViewById(com.example.hire_me_test.R.id.editLocation);
        editSalary = findViewById(com.example.hire_me_test.R.id.editSalary);
        editOtSalary = findViewById(com.example.hire_me_test.R.id.editOtSalary);
        editRequirements = findViewById(com.example.hire_me_test.R.id.editRequirements);
        editDate = findViewById(com.example.hire_me_test.R.id.editDate);
        editPickup = findViewById(com.example.hire_me_test.R.id.editPickup);
        editContact = findViewById(com.example.hire_me_test.R.id.editContact);
        btnPostJob = findViewById(com.example.hire_me_test.R.id.btnPostJob);
        btnDatePicker = findViewById(R.id.btnDatePicker);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Get employeeId from intent extras (key: "employer_id")
        employeeId = getIntent().getStringExtra("employer_id");
        if (employeeId == null) {
            employeeId = "";
        }

        // Setup spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, jobTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobTitle.setAdapter(adapter);

        // Disable keyboard input for time EditTexts and set click listeners to show time pickers
        editStartTime.setKeyListener(null);
        editStartTime.setFocusable(false);
        editStartTime.setClickable(true);
        editStartTime.setOnClickListener(v -> showTimePicker(editStartTime));

        editEndTime.setKeyListener(null);
        editEndTime.setFocusable(false);
        editEndTime.setClickable(true);
        editEndTime.setOnClickListener(v -> showTimePicker(editEndTime));

        // Date picker dialog
        btnDatePicker.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(PostJobActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editDate.setText(date);
                    }, year, month, day);
            dialog.show();
        });

        // Post job button click listener
        btnPostJob.setOnClickListener(v -> postJob());
    }

    // Show TimePickerDialog and set selected time to passed EditText
    private void showTimePicker(EditText timeEditText) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(PostJobActivity.this,
                (view, hourOfDay, minute1) -> {
                    // Format time as hh:mm AM/PM
                    String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                    int hour12 = hourOfDay % 12;
                    if (hour12 == 0) hour12 = 12;
                    String formattedMinute = (minute1 < 10) ? "0" + minute1 : String.valueOf(minute1);
                    String time = hour12 + ":" + formattedMinute + " " + amPm;

                    timeEditText.setText(time);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void postJob() {
        String jobTitleInput = editCustomTitle.getText().toString().trim();
        if (jobTitleInput.isEmpty()) {
            jobTitleInput = spinnerJobTitle.getSelectedItem().toString();
            // Prevent using the placeholder option "Select the Job Title"
            if (jobTitleInput.equals("Select the Job Title")) {
                Toast.makeText(this, "Please select a valid job title or enter a custom title", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String startTime = editStartTime.getText().toString().trim();
        String endTime = editEndTime.getText().toString().trim();
        String fullTime = startTime + " - " + endTime;

        String vacancies = editVacancies.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String basicSalary = editSalary.getText().toString().trim();
        String otSalary = editOtSalary.getText().toString().trim();
        String requirements = editRequirements.getText().toString().trim();
        String jobDate = editDate.getText().toString().trim();
        String pickupLocation = editPickup.getText().toString().trim();
        String contactInfo = editContact.getText().toString().trim();

        String employerEmail = getSharedPreferences("user", MODE_PRIVATE).getString("email", "");

        // Basic validation
        if (jobTitleInput.isEmpty() || vacancies.isEmpty() || startTime.isEmpty() || endTime.isEmpty() ||
                location.isEmpty() || basicSalary.isEmpty() || jobDate.isEmpty() || contactInfo.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting...");
        pd.setCancelable(false);
        pd.show();

        String finalJobTitle = jobTitleInput;
        String finalTime = fullTime;

        StringRequest request = new StringRequest(Request.Method.POST, postJobUrl, response -> {
            pd.dismiss();
            Toast.makeText(PostJobActivity.this, "Job Posted Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }, error -> {
            pd.dismiss();
            Toast.makeText(PostJobActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("job_title", finalJobTitle);
                map.put("vacancies", vacancies);
                map.put("time", finalTime);
                map.put("location", location);
                map.put("basic_salary", basicSalary);
                map.put("ot_salary", otSalary);
                map.put("requirements", requirements);
                map.put("job_date", jobDate);
                map.put("pickup_location", pickupLocation);
                map.put("contact_info", contactInfo);
                map.put("email", employerEmail);
                map.put("employee_id", employeeId);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
