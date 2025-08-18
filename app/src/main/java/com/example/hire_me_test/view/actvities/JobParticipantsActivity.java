package com.example.hire_me_test.view.actvities;

import android.os.Bundle;
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

        // Back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());  // Finish activity on click

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewParticipants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        participantList = new ArrayList<>();

        // Get job ID from intent
        jobId = getIntent().getIntExtra("job_id", -1);

        // Fetch participants
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

                        adapter = new ParticipantAdapter(participantList);
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
}
