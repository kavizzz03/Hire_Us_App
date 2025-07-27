package com.example.hire_me_test;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ViewHolder> {

    private final Context context;
    private final List<Worker> workerList;
    private int vacancies;
    private final Set<String> alreadyHired;
    private final OnWorkerClickListener listener;
    private final Set<Integer> selectedPositions = new HashSet<>();

    public interface OnWorkerClickListener {
        void onClick(Worker worker);
    }

    public ApplicantAdapter(Context context, List<Worker> workerList, int vacancies, Set<String> alreadyHired, OnWorkerClickListener listener) {
        this.context = context;
        this.workerList = workerList;
        this.vacancies = vacancies;
        this.alreadyHired = alreadyHired;
        this.listener = listener;
    }

    public void setVacancies(int vacancies) {
        this.vacancies = vacancies;
        if (selectedPositions.size() > vacancies) {
            selectedPositions.clear();
            notifyDataSetChanged();
            Toast.makeText(context, "Selection reset due to vacancies limit", Toast.LENGTH_SHORT).show();
        }
    }

    public List<Worker> getSelectedWorkers() {
        List<Worker> selected = new ArrayList<>();
        for (Integer pos : selectedPositions) {
            selected.add(workerList.get(pos));
        }
        return selected;
    }

    @NonNull
    @Override
    public ApplicantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_applicant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantAdapter.ViewHolder holder, int position) {
        Worker worker = workerList.get(position);

        holder.txtName.setText(worker.getFullName());
        holder.txtEmail.setText(worker.getEmail());
        holder.txtContact.setText("Contact: " + worker.getContactNumber());
        holder.txtRating.setText("Rating: " + worker.getRating());

        String status = worker.getStatus();

        // Set status display
        if ("hired".equalsIgnoreCase(status)) {
            holder.applicantStatus.setText("Hired");
            holder.applicantStatus.setTextColor(Color.RED);
            holder.txtHiredStatus.setVisibility(View.VISIBLE);
            holder.txtHiredStatus.setText("Already Hired");

            holder.checkbox.setEnabled(false);
            holder.checkbox.setChecked(true); // visually show already hired
        } else {
            holder.applicantStatus.setText("Available");
            holder.applicantStatus.setTextColor(Color.parseColor("#388E3C"));
            holder.txtHiredStatus.setVisibility(View.GONE);

            holder.checkbox.setEnabled(true);
            holder.checkbox.setOnCheckedChangeListener(null); // prevent recycled listener issue
            holder.checkbox.setChecked(selectedPositions.contains(position));

            holder.checkbox.setOnClickListener(v -> {
                if (holder.checkbox.isChecked()) {
                    if (selectedPositions.size() >= vacancies) {
                        holder.checkbox.setChecked(false);
                        Toast.makeText(context, "You can select up to " + vacancies + " applicants only", Toast.LENGTH_SHORT).show();
                    } else {
                        selectedPositions.add(position);
                    }
                } else {
                    selectedPositions.remove(position);
                }
            });
        }

        // Open worker details on item click
        holder.itemView.setOnClickListener(v -> listener.onClick(worker));
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail, txtContact, txtRating, txtHiredStatus, applicantStatus;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.applicantName);
            txtEmail = itemView.findViewById(R.id.applicantEmail);
            txtContact = itemView.findViewById(R.id.applicantContact);
            txtRating = itemView.findViewById(R.id.applicantRating);
            txtHiredStatus = itemView.findViewById(R.id.alreadyHiredLabel); // Make sure it's in your XML
            applicantStatus = itemView.findViewById(R.id.applicantHiredStatus); // Also ensure it's in XML
            checkbox = itemView.findViewById(R.id.applicantCheckbox);
        }
    }
}
