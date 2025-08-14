package com.example.hire_me_test.view.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.WithdrawRequestModel;

import java.util.List;

public class WithdrawRequestAdapter extends RecyclerView.Adapter<WithdrawRequestAdapter.ViewHolder> {

    public interface OnStatusChangeListener {
        void onChange(int requestId, String newStatus);
    }

    private List<WithdrawRequestModel> list;
    private OnStatusChangeListener listener;

    public WithdrawRequestAdapter(List<WithdrawRequestModel> list, OnStatusChangeListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdraw_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WithdrawRequestModel item = list.get(position);
        holder.txtIdNumber.setText("ID: " + item.getIdNumber());
        holder.txtAmount.setText("Amount: $" + item.getAmount());
        holder.txtStatus.setText("Status: " + item.getStatus());

        holder.btnProceed.setOnClickListener(v -> listener.onChange(item.getId(), "Proceed"));
        holder.btnFinish.setOnClickListener(v -> listener.onChange(item.getId(), "Finished"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtIdNumber, txtAmount, txtStatus;
        Button btnProceed, btnFinish;

        ViewHolder(View itemView) {
            super(itemView);
            txtIdNumber = itemView.findViewById(R.id.txtIdNumber);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnProceed = itemView.findViewById(R.id.btnProceed);
            btnFinish = itemView.findViewById(R.id.btnFinish);
        }
    }
}
