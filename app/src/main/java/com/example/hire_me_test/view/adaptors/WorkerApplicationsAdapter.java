package com.example.hire_me_test.view.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.WorkerApplication;
import com.example.hire_me_test.view.actvities.WorkerApplicationsActivity;

import java.util.List;
import okhttp3.*;
import java.io.IOException;

public class WorkerApplicationsAdapter extends RecyclerView.Adapter<WorkerApplicationsAdapter.ViewHolder> {

    private Context context;
    private List<WorkerApplication> applications;
    private String workerId;

    public WorkerApplicationsAdapter(Context context, List<WorkerApplication> applications, String workerId){
        this.context = context;
        this.applications = applications;
        this.workerId = workerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_worker_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        WorkerApplication a = applications.get(position);
        holder.tvJobTitle.setText(a.getJobTitle());
        holder.tvCompany.setText(a.getCompanyName());
        holder.tvAppliedAt.setText("Applied at: " + a.getAppliedAt());

        holder.btnDelete.setOnClickListener(v -> deleteApplication(a.getId(), position));
    }

    private void deleteApplication(int id, int position){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("application_id", String.valueOf(id))
                .add("id_number", workerId)
                .build();
        Request request = new Request.Builder()
                .url("https://hireme.cpsharetxt.com/delete_worker_application.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e){ e.printStackTrace(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                if(res.contains("success")){
                    ((WorkerApplicationsActivity)context).runOnUiThread(() -> {
                        applications.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Application deleted", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount(){ return applications.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvJobTitle, tvCompany, tvAppliedAt;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvAppliedAt = itemView.findViewById(R.id.tvAppliedAt);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
