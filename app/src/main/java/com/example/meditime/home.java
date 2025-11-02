package com.example.meditime;

import com.example.meditime.Medication;
import com.example.meditime.OnMedicationActionListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.meditime.MedicationDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;



public class home extends AppCompatActivity implements OnMedicationActionListener {


    private MedicationDbHelper dbHelper;
    private MedicationAdapter medicationAdapter;
    private RecyclerView recyclerViewMedications;
    private List<Medication> medicationList = new ArrayList<>();


    private final int DAYS_TO_SHOW = 60;
    private int initialSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        dbHelper = new MedicationDbHelper(this);


        recyclerViewMedications = findViewById(R.id.recyclerViewMedications);


        if (recyclerViewMedications == null) {
            Toast.makeText(this, "Error: RecyclerView de medicamentos no encontrado.", Toast.LENGTH_LONG).show();
        } else {
            recyclerViewMedications.setLayoutManager(new LinearLayoutManager(this));


            medicationAdapter = new MedicationAdapter(this, medicationList, this);
            recyclerViewMedications.setAdapter(medicationAdapter);
        }


        ImageButton btnAgregar = findViewById(R.id.btnOption2);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, AddMedicationActivity.class);
                startActivity(intent);
            }
        });


        updateCurrentDayLabel();


        setupHorizontalCalendar();

        ImageView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerViewMedications != null) {
            loadMedications();
        }
    }

    private void loadMedications() {
        List<Medication> newList = dbHelper.getAllMedications();

        medicationList.clear();
        medicationList.addAll(newList);

        if (medicationAdapter != null) {
            medicationAdapter.updateList(medicationList);
        }

        TextView txtNoMedicines = findViewById(R.id.txtNoMedicines);
        if (txtNoMedicines != null) {
            if (medicationList.isEmpty()) {
                txtNoMedicines.setVisibility(View.VISIBLE);
            } else {
                txtNoMedicines.setVisibility(View.GONE);
            }
        }
    }


    private void updateCurrentDayLabel() {
        TextView txtCurrentDayLabel = findViewById(R.id.txtCurrentDayLabel);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d", new Locale("es", "ES"));
        Date currentDate = new Date();
        String formattedDate = dateFormat.format(currentDate);

        if (formattedDate.length() > 0) {
            formattedDate = formattedDate.substring(0, 1).toUpperCase(new Locale("es", "ES")) + formattedDate.substring(1);
        }
        txtCurrentDayLabel.setText("Hoy, " + formattedDate);
    }

    private void setupHorizontalCalendar() {
        RecyclerView horizontalCalendarRecycler = findViewById(R.id.horizontalCalendarRecycler);

        if (horizontalCalendarRecycler == null) {
            return;
        }

        List<Calendar> daysList = generateDaysList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horizontalCalendarRecycler.setLayoutManager(layoutManager);


        CalendarAdapter adapter = new CalendarAdapter(daysList, initialSelectedPosition);
        horizontalCalendarRecycler.setAdapter(adapter);

        horizontalCalendarRecycler.scrollToPosition(initialSelectedPosition);
    }


    private List<Calendar> generateDaysList() {
        List<Calendar> daysList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
        int offsetBefore = DAYS_TO_SHOW / 2;
        initialSelectedPosition = offsetBefore;

        calendar.add(Calendar.DAY_OF_YEAR, -offsetBefore);

        for (int i = 0; i < DAYS_TO_SHOW; i++) {
            Calendar day = (Calendar) calendar.clone();
            daysList.add(day);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return daysList;
    }


    @Override
    public void onDeleteClick(Medication medication) {

        long medicationId = medication.getId();


        int rowsDeleted = dbHelper.deleteMedication(medicationId);

        if (rowsDeleted > 0) {
            Toast.makeText(this, medication.getName() + " eliminado correctamente.", Toast.LENGTH_SHORT).show();

            loadMedications();
        } else {
            Toast.makeText(this, "Error al eliminar el medicamento.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onEditClick(Medication medication) {

        Toast.makeText(this, "Abriendo edición para: " + medication.getName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(home.this, AddMedicationActivity.class);

        intent.putExtra("MEDICATION_ID", medication.getId());
        startActivity(intent);
    }
}