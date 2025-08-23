package com.example.hire_me_test.view.actvities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.Participant;
import com.example.hire_me_test.view.adaptors.ParticipantAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JobParticipantsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Participant> participantList;
    ParticipantAdapter adapter;
    int jobId;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_participants);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewParticipants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        participantList = new ArrayList<>();

        jobId = getIntent().getIntExtra("job_id", -1);

        fetchParticipants();
    }

    private void fetchParticipants() {
        String url = "https://hireme.cpsharetxt.com/get_job_participants.php?job_id=" + jobId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            Participant p = new Participant(
                                    obj.getString("id_number"),
                                    obj.getString("name"),
                                    obj.getString("contact_number")
                            );
                            participantList.add(p);
                        }

                        adapter = new ParticipantAdapter(this, participantList, this::showRatingDialog);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void showRatingDialog(Participant participant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate Worker");

        LayoutInflater inflater = LayoutInflater.from(this);
        final android.view.View dialogView = inflater.inflate(R.layout.dialog_worker_rating, null);
        builder.setView(dialogView);

        EditText edtRatedBy = dialogView.findViewById(R.id.edtRatedBy);
        EditText edtRating = dialogView.findViewById(R.id.edtRating);
        EditText edtExperience = dialogView.findViewById(R.id.edtExperience);
        EditText edtFeedback = dialogView.findViewById(R.id.edtFeedback);
        EditText edtJobTitle = dialogView.findViewById(R.id.edtJobTitle);
        EditText edtCompanyName = dialogView.findViewById(R.id.edtCompanyName);
        EditText edtDuration = dialogView.findViewById(R.id.edtDuration);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            sendWorkerRating(
                    participant.getIdNumber(),  // send ID number
                    edtRatedBy.getText().toString(),
                    edtRating.getText().toString(),
                    edtExperience.getText().toString(),
                    edtFeedback.getText().toString(),
                    edtJobTitle.getText().toString(),
                    edtCompanyName.getText().toString(),
                    edtDuration.getText().toString()
            );
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendWorkerRating(String idNumber, String ratedBy, String rating,
                                  String experience, String feedback, String jobTitle,
                                  String companyName, String duration) {

        String url = "https://hireme.cpsharetxt.com/add_worker_rating.php" +
                "?id_number=" + idNumber +
                "&rated_by=" + ratedBy +
                "&rating=" + rating +
                "&work_experience=" + experience +
                "&feedback=" + feedback +
                "&job_title=" + jobTitle +
                "&company_name=" + companyName +
                "&duration=" + duration;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> Toast.makeText(this, "Rating saved", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
