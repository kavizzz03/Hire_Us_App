package com.example.hire_me_test.view.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.JobHistory;

import java.util.List;

public class JobHistoryAdapter extends RecyclerView.Adapter<JobHistoryAdapter.ViewHolder> {

    public interface OnJobClickListener {
        void onJobClick(JobHistory job);
    }

    private List<JobHistory> jobList;
    private OnJobClickListener listener;

    public JobHistoryAdapter(List<JobHistory> jobList, OnJobClickListener listener) {
        this.jobList = jobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobHistory job = jobList.get(position);
        holder.title.setText(job.getJobTitle());
        holder.date.setText(job.getDate());
        holder.location.setText(job.getLocation());

        holder.itemView.setOnClickListener(v -> listener.onJobClick(job));
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, location;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textJobTitle);
            date = itemView.findViewById(R.id.textJobDate);
            location = itemView.findViewById(R.id.textJobLocation);
        }
    }
}
