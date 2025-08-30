package com.example.hire_me_test.view.adaptors;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
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

        // Default meals count = 0
        holder.txtMealsCount.setText("Meals: 0");

        // Fetch meals count
        fetchMealsCount(job.getId(), holder);

        // View applicants
        holder.btnViewApplicants.setOnClickListener(v -> applicantsListener.onViewApplicantsClick(job));

        // Add meal -> show input dialog
        holder.btnAddMeal.setOnClickListener(v -> showAddMealDialog(job, holder));

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

    // 🔹 Show dialog to add meal
    private void showAddMealDialog(JobModel job, JobViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Meal");

        // Inflate custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_meal, null);
        EditText edtMealName = dialogView.findViewById(R.id.edtMealName);
        EditText edtMealDescription = dialogView.findViewById(R.id.edtMealDescription);
        EditText edtMealPrice = dialogView.findViewById(R.id.edtMealPrice);

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String mealName = edtMealName.getText().toString().trim();
            String mealDescription = edtMealDescription.getText().toString().trim();
            String mealPrice = edtMealPrice.getText().toString().trim();

            if (mealName.isEmpty() || mealDescription.isEmpty() || mealPrice.isEmpty()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            sendMealToServer(job, mealName, mealDescription, mealPrice, holder);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // 🔹 Send meal to server
    private void sendMealToServer(JobModel job, String mealName, String mealDescription, String mealPrice, JobViewHolder holder) {
        String url = "https://hireme.cpsharetxt.com/add_meal.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);
                        String message = jsonResponse.optString("message", "Unknown response");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        if (success) {
                            fetchMealsCount(job.getId(), holder); // refresh meals count
                        }
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
                params.put("meal_name", mealName);
                params.put("description", mealDescription);
                params.put("meal_price", mealPrice);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    // 🔹 Fetch total meals count
    private void fetchMealsCount(int jobId, JobViewHolder holder) {
        String url = "https://hireme.cpsharetxt.com/get_meals_count.php?job_id=" + jobId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.optBoolean("success")) {
                            int meals = obj.optInt("meals_count", 0);
                            holder.txtMealsCount.setText("Meals: " + meals);
                        } else {
                            holder.txtMealsCount.setText("Meals: 0");
                        }
                    } catch (Exception e) {
                        holder.txtMealsCount.setText("Meals: 0");
                    }
                },
                error -> holder.txtMealsCount.setText("Meals: 0")
        );

        Volley.newRequestQueue(context).add(request);
    }

    // ✅ ViewHolder class
    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView txtJobTitle, txtCompany, txtSalary, txtMealsCount;
        Button btnViewApplicants, btnAddMeal, btnFinishJob;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJobTitle = itemView.findViewById(R.id.txtJobTitle);
            txtCompany = itemView.findViewById(R.id.txtCompany);
            txtSalary = itemView.findViewById(R.id.txtSalary);
            txtMealsCount = itemView.findViewById(R.id.txtMealsCount);
            btnViewApplicants = itemView.findViewById(R.id.btnViewApplicants);
            btnAddMeal = itemView.findViewById(R.id.btnAddMeal);
            btnFinishJob = itemView.findViewById(R.id.btnFinishJob);
        }
    }
}
