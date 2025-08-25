package com.example.hire_me_test.view.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.JobHistoryModel;
import java.util.ArrayList;

public class EmpJobHistoryAdapter extends RecyclerView.Adapter<EmpJobHistoryAdapter.ViewHolder> {

    private final ArrayList<JobHistoryModel> jobHistoryList;

    public EmpJobHistoryAdapter(ArrayList<JobHistoryModel> jobHistoryList) {
        this.jobHistoryList = jobHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_history_emp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobHistoryModel job = jobHistoryList.get(position);
        holder.tvJobId.setText("Job ID: " + job.getJobId());
        holder.tvHiredAt.setText("Hired At: " + job.getHiredAt());
        holder.tvWantsMeals.setText("Wants Meals: " + job.getWantsMeals());
    }

    @Override
    public int getItemCount() {
        return jobHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobId, tvHiredAt, tvWantsMeals;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobId = itemView.findViewById(R.id.tvJobId);
            tvHiredAt = itemView.findViewById(R.id.tvHiredAt);
            tvWantsMeals = itemView.findViewById(R.id.tvWantsMeals);
        }
    }
}
