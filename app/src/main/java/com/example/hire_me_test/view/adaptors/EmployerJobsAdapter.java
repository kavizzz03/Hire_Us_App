package com.example.hire_me_test.view.adaptors;

import android.content.Context;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.JobModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployerJobsAdapter extends RecyclerView.Adapter<EmployerJobsAdapter.JobViewHolder> {

    public interface OnViewApplicantsClickListener {
        void onViewApplicantsClick(JobModel job);
    }

    public interface OnFinishJobClickListener {
        void onFinishJobClick(JobModel job);
    }

    private final Context context;
    private final List<JobModel> jobList;
    private final OnViewApplicantsClickListener applicantsListener;
    private final OnFinishJobClickListener finishJobListener;

    public EmployerJobsAdapter(Context context,
                               List<JobModel> jobList,
                               OnViewApplicantsClickListener applicantsListener,
                               OnFinishJobClickListener finishJobListener) {
        this.context = context;
        this.jobList = jobList;
        this.applicantsListener = applicantsListener;
        this.finishJobListener = finishJobListener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobModel job = jobList.get(position);

        holder.txtJobTitle.setText(job.getJobTitle() != null ? job.getJobTitle() : "N/A");
        holder.txtCompany.setText("Date: " + (job.getJobDate() != null ? job.getJobDate() : "N/A"));
        holder.txtSalary.setText("Rs. " + (job.getBasicSalary() != null ? job.getBasicSalary() : "0"));

        // View applicants
        holder.btnViewApplicants.setOnClickListener(v -> applicantsListener.onViewApplicantsClick(job));

        // Add meal
        holder.btnAddMeal.setOnClickListener(v -> sendMealToServer(job));

        // Finish job
        holder.btnFinishJob.setOnClickListener(v -> {
            if (finishJobListener != null) {
                finishJobListener.onFinishJobClick(job);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList != null ? jobList.size() : 0;
    }

    private void sendMealToServer(JobModel job) {
        String url = "https://hireme.cpsharetxt.com/add_meal.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);
                        String message = jsonResponse.optString("message", "Unknown response");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(context, "Response parse error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("job_id", String.valueOf(job.getId()));
                params.put("job_title", job.getJobTitle() != null ? job.getJobTitle() : "");
                params.put("company_name", job.getCompanyName() != null ? job.getCompanyName() : "");
                params.put("basic_salary", job.getBasicSalary() != null ? job.getBasicSalary() : "0");
                params.put("job_date", job.getJobDate() != null ? job.getJobDate() : "");
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView txtJobTitle, txtCompany, txtSalary;
        Button btnViewApplicants, btnAddMeal, btnFinishJob;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJobTitle = itemView.findViewById(R.id.txtJobTitle);
            txtCompany = itemView.findViewById(R.id.txtCompany);
            txtSalary = itemView.findViewById(R.id.txtSalary);
            btnViewApplicants = itemView.findViewById(R.id.btnViewApplicants);
            btnAddMeal = itemView.findViewById(R.id.btnAddMeal);
            btnFinishJob = itemView.findViewById(R.id.btnFinishJob);
        }
    }
}
