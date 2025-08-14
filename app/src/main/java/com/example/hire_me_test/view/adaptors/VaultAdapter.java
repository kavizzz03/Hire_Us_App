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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.VaultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaultAdapter extends RecyclerView.Adapter<VaultAdapter.VaultViewHolder> {

    private Context context;
    private List<VaultModel> vaultList;
    private String workerIdNumber;  // Worker ID passed from activity

    public VaultAdapter(Context context, List<VaultModel> vaultList, String workerIdNumber) {
        this.context = context;
        this.vaultList = vaultList;
        this.workerIdNumber = workerIdNumber;
    }

    @NonNull
    @Override
    public VaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vault, parent, false);
        return new VaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaultViewHolder holder, int position) {
        VaultModel model = vaultList.get(position);

        holder.jobIdText.setText("Job ID: " + model.jobId);
        holder.salaryText.setText("Salary: Rs. " + model.salary);
        holder.otHoursText.setText("OT Hours: " + model.otHours);
        holder.otSalaryText.setText("OT Salary: Rs. " + model.otSalary);
        holder.totalText.setText("Total: Rs. " + model.total);
        holder.updatedAtText.setText("Updated At: " + model.updatedAt);

        // Reset button enabled state in case view is recycled
        holder.btnWithdraw.setEnabled(true);

        holder.btnWithdraw.setOnClickListener(v -> {
            holder.btnWithdraw.setEnabled(false);
            sendWithdrawRequest(model.jobId, model.total, holder.btnWithdraw);
        });
    }

    @Override
    public int getItemCount() {
        return vaultList.size();
    }

    private void sendWithdrawRequest(String jobId, double amount, Button withdrawButton) {
        String url = "https://hireme.cpsharetxt.com/request_withdraw.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.has("success")) {
                            Toast.makeText(context, json.getString("success"), Toast.LENGTH_LONG).show();
                            // Disable button permanently after success
                            withdrawButton.setEnabled(false);
                        } else if (json.has("already_requested")) {
                            Toast.makeText(context, json.getString("already_requested"), Toast.LENGTH_LONG).show();
                            withdrawButton.setEnabled(false); // Disable because already requested
                        } else if (json.has("warning")) {
                            Toast.makeText(context, json.getString("warning"), Toast.LENGTH_LONG).show();
                            withdrawButton.setEnabled(true); // Allow retry
                        } else if (json.has("error")) {
                            Toast.makeText(context, json.getString("error"), Toast.LENGTH_LONG).show();
                            withdrawButton.setEnabled(true); // Allow retry
                        } else {
                            Toast.makeText(context, "Unknown server response", Toast.LENGTH_LONG).show();
                            withdrawButton.setEnabled(true); // Allow retry just in case
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Response parse error", Toast.LENGTH_LONG).show();
                        withdrawButton.setEnabled(true);
                    }
                },
                error -> {
                    Toast.makeText(context, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    withdrawButton.setEnabled(true);
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("job_id", jobId);
                params.put("amount", String.valueOf(amount));
                params.put("id_number", workerIdNumber); // Make sure this is correctly initialized
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    static class VaultViewHolder extends RecyclerView.ViewHolder {
        TextView jobIdText, salaryText, otHoursText, otSalaryText, totalText, updatedAtText;
        Button btnWithdraw;

        public VaultViewHolder(@NonNull View itemView) {
            super(itemView);
            jobIdText = itemView.findViewById(R.id.jobIdText);
            salaryText = itemView.findViewById(R.id.salaryText);
            otHoursText = itemView.findViewById(R.id.otHoursText);
            otSalaryText = itemView.findViewById(R.id.otSalaryText);
            totalText = itemView.findViewById(R.id.totalText);
            updatedAtText = itemView.findViewById(R.id.updatedAtText);
            btnWithdraw = itemView.findViewById(R.id.btnWithdraw);
        }
    }
}
