package com.example.meditime;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meditime.Medication;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<Medication> medicationList;
    private Context context;

    private OnMedicationActionListener listener;


    public MedicationAdapter(Context context, List<Medication> medicationList, OnMedicationActionListener listener) {
        this.context = context;
        this.medicationList = medicationList;
        this.listener = listener;
    }


    public MedicationAdapter(Context context, List<Medication> medicationList) {
        this.context = context;
        this.medicationList = medicationList;
        this.listener = null;
    }


    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMedicineName;
        public TextView txtDose;
        public TextView txtTime;
        public CardView medicineCard;
        public ImageButton btnEdit;
        public ImageButton btnDelete;

        public MedicationViewHolder(View itemView) {
            super(itemView);
            txtMedicineName = itemView.findViewById(R.id.txtMedicineName);
            txtDose = itemView.findViewById(R.id.txtDose);
            txtTime = itemView.findViewById(R.id.txtTime);
            medicineCard = itemView.findViewById(R.id.medicineCard);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }


    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicamento, parent, false);
        return new MedicationViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication currentMed = medicationList.get(position);

        holder.txtMedicineName.setText(currentMed.getName());
        holder.txtDose.setText(String.valueOf(currentMed.getDosesPerDay()));
        holder.txtTime.setText(currentMed.getInitialTime());


        holder.medicineCard.setCardBackgroundColor(currentMed.getColor());



        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(currentMed);
            }
        });


        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(currentMed);
            }
        });
    }


    @Override
    public int getItemCount() {
        return medicationList.size();
    }


    public void updateList(List<Medication> newList) {
        medicationList = newList;
        notifyDataSetChanged();
    }
}