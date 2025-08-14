package com.example.hire_me_test.view.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.JobModel;

import java.util.List;

public class EmployerJobsAdapter extends RecyclerView.Adapter<EmployerJobsAdapter.JobViewHolder> {

    // Interfaces for different click actions
    public interface OnViewApplicantsClickListener {
        void onViewApplicantsClick(JobModel job);
    }

    public interface OnAddMealsClickListener {
        void onAddMealsClick(JobModel job);
    }

    public interface OnFinishJobClickListener {
        void onFinishJobClick(JobModel job);
    }

    private final Context context;
    private final List<JobModel> jobList;
    private final OnViewApplicantsClickListener applicantsListener;
    private final OnAddMealsClickListener mealsListener;
    private final OnFinishJobClickListener finishJobListener;

    // Constructor
    public EmployerJobsAdapter(Context context,
                               List<JobModel> jobList,
                               OnViewApplicantsClickListener applicantsListener,
                               OnAddMealsClickListener mealsListener,
                               OnFinishJobClickListener finishJobListener) {
        this.context = context;
        this.jobList = jobList;
        this.applicantsListener = applicantsListener;
        this.mealsListener = mealsListener;
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

        // Set values to UI
        holder.txtJobTitle.setText(job.getJobTitle());
        holder.txtCompany.setText("Date: " + job.getJobDate());
        holder.txtSalary.setText("Rs. " + job.getBasicSalary());

        // Set listeners
        holder.btnViewApplicants.setOnClickListener(v -> applicantsListener.onViewApplicantsClick(job));
        holder.btnAddMeal.setOnClickListener(v -> mealsListener.onAddMealsClick(job));
        holder.btnFinishJob.setOnClickListener(v -> {
            if (finishJobListener != null) {
                finishJobListener.onFinishJobClick(job);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
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
