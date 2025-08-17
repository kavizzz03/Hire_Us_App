package com.example.hire_me_test.view.adaptors;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import com.example.hire_me_test.model.model.data.WithdrawRequest;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithdrawRequestAdapter extends RecyclerView.Adapter<WithdrawRequestAdapter.ViewHolder> {

    private final List<WithdrawRequest> list;
    private final Context context;

    public WithdrawRequestAdapter(List<WithdrawRequest> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.withdraw_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WithdrawRequest item = list.get(position);
        holder.tvJobInfo.setText("Job ID: " + item.getJobId() + " | Amount: LKR " + item.getAmount());
        holder.tvStatus.setText("Status: " + item.getStatus());

        if(item.getStatus().equals("Completed")){
            holder.btnWithdraw.setEnabled(false);
            holder.btnWithdraw.setText("Withdrawn");
        } else {
            holder.btnWithdraw.setEnabled(true);
            holder.btnWithdraw.setText("Mark as Withdrawn");
            holder.btnWithdraw.setOnClickListener(v -> markAsWithdrawn(item.getId(), position));
        }
    }

    private void markAsWithdrawn(int withdrawId, int position){
        String url = "https://hireme.cpsharetxt.com/process_withdraw_request.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        if(obj.getString("status").equals("success")){
                            list.get(position).setStatus("Completed");
                            notifyItemChanged(position);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("withdraw_id", String.valueOf(withdrawId));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobInfo, tvStatus;
        Button btnWithdraw;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobInfo = itemView.findViewById(R.id.tvJobInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnWithdraw = itemView.findViewById(R.id.btnWithdraw);
        }
    }
}
