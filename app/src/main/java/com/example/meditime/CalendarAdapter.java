package com.example.meditime;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {


    private List<Calendar> daysList;
    private int selectedPosition;
    private Context context;

    public CalendarAdapter(List<Calendar> daysList, int selectedPosition) {
        this.daysList = daysList;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.item_day_picker, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Calendar day = daysList.get(position);


        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE", new Locale("es", "ES"));
        SimpleDateFormat dayOfMonthFormat = new SimpleDateFormat("d", new Locale("es", "ES"));


        String dayOfWeek = dayOfWeekFormat.format(day.getTime()).toUpperCase(new Locale("es", "ES"));

        holder.txtDayOfWeek.setText(dayOfWeek.substring(0, 3));
        holder.txtDayOfMonth.setText(dayOfMonthFormat.format(day.getTime()));


        if (position == selectedPosition) {



            holder.txtDayOfWeek.setBackgroundResource(0);
            holder.txtDayOfWeek.setTextColor(Color.BLACK);


            holder.txtDayOfMonth.setBackgroundResource(R.drawable.day_circle_selected);
            holder.txtDayOfMonth.setTextColor(Color.WHITE);

        } else {


            holder.txtDayOfWeek.setBackgroundResource(0);
            holder.txtDayOfWeek.setTextColor(Color.parseColor("#808080"));


            holder.txtDayOfMonth.setBackgroundResource(0);
            holder.txtDayOfMonth.setTextColor(Color.BLACK);
        }

        // Configurar el click listener
        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = position;
            notifyItemChanged(selectedPosition);


        });
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }


    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView txtDayOfWeek;
        TextView txtDayOfMonth;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDayOfWeek = itemView.findViewById(R.id.txtDayOfWeek);
            txtDayOfMonth = itemView.findViewById(R.id.txtDayOfMonth);
        }
    }
}
