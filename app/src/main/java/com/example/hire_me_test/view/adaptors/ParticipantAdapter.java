package com.example.hire_me_test.view.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.Participant;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private List<Participant> list;
    private Context context;
    private OnParticipantClickListener listener;

    public interface OnParticipantClickListener {
        void onParticipantClick(Participant participant);
    }

    public ParticipantAdapter(Context context, List<Participant> list, OnParticipantClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participant p = list.get(position);
        holder.name.setText(p.getName());
        holder.contact.setText(p.getContact());
        holder.idNumber.setText("ID: " + p.getIdNumber());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onParticipantClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, contact, idNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            contact = itemView.findViewById(R.id.textContact);
            idNumber = itemView.findViewById(R.id.textIdNumber);
        }
    }
}
