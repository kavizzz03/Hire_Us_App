package com.example.hire_me_test;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    List<Job> jobList;
    List<Job> jobListFull;  // for filtering
    OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    public JobAdapter(List<Job> jobList, OnJobClickListener listener) {
        this.jobList = jobList;
        this.jobListFull = new ArrayList<>(jobList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.textJobTitle.setText(job.getJobTitle());
        holder.textCompany.setText(job.getCompanyName());
        holder.textSalary.setText("Rs. " + job.getBasicSalary());
        holder.textLocation.setText(job.getLocation());

        holder.itemView.setOnClickListener(v -> listener.onJobClick(job));
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void filterList(List<Job> filteredList) {
        jobList = filteredList;
        notifyDataSetChanged();
    }

    class JobViewHolder extends RecyclerView.ViewHolder {
        TextView textJobTitle, textCompany, textSalary, textLocation;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            textJobTitle = itemView.findViewById(R.id.textJobTitle);
            textCompany = itemView.findViewById(R.id.textCompany);
            textSalary = itemView.findViewById(R.id.textSalary);
            textLocation = itemView.findViewById(R.id.textLocation);
        }
    }
}
