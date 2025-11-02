package com.example.meditime;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meditime.MedicationDbHelper;
import com.example.meditime.Medication;

import java.util.Calendar;
import java.util.Locale;

public class AddMedicationActivity extends AppCompatActivity {

    private EditText etMedicationName;
    private EditText etDosesPerDay;
    private TextView txtInitialTime;
    private Spinner spinnerInterval;
    private Spinner spinnerFrequency;
    private Spinner spinnerAmPm;
    private ImageView imgColorPicker;
    private Button btnAddMedication;

    private MedicationDbHelper dbHelper;
    private long medicationIdToEdit = -1;
    private int selectedColor = Color.parseColor("#80DEEA");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_med);


        dbHelper = new MedicationDbHelper(this);


        etMedicationName = findViewById(R.id.etMedicationName);
        etDosesPerDay = findViewById(R.id.etDosesPerDay);
        txtInitialTime = findViewById(R.id.txtInitialTime);
        spinnerInterval = findViewById(R.id.spinnerInterval);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        spinnerAmPm = findViewById(R.id.spinnerAmPm);
        imgColorPicker = findViewById(R.id.imgColorPicker);
        btnAddMedication = findViewById(R.id.btnAddMedication);

        imgColorPicker.setBackgroundColor(selectedColor);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            medicationIdToEdit = extras.getLong("MEDICATION_ID", -1);

            if (medicationIdToEdit != -1) {

                btnAddMedication.setText("Guardar Cambios");
                loadMedicationData(medicationIdToEdit);
            } else {

                btnAddMedication.setText("Agregar Medicamento");
            }
        }



        txtInitialTime.setOnClickListener(v -> showTimePickerDialog());
        imgColorPicker.setOnClickListener(v -> showColorPickerDialog());
        btnAddMedication.setOnClickListener(v -> saveOrUpdateMedication()); // Lógica unificada
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }


    private void loadMedicationData(long id) {
        Medication medication = dbHelper.getMedication(id);

        if (medication != null) {
            etMedicationName.setText(medication.getName());
            etDosesPerDay.setText(String.valueOf(medication.getDosesPerDay()));


            String fullTime = medication.getInitialTime(); // Ejemplo: "08:00 AM"
            if (fullTime != null && fullTime.contains(" ")) {
                String[] timeParts = fullTime.split(" ");
                txtInitialTime.setText(timeParts[0]);


                String amPm = timeParts[1];
                int amPmIndex = amPm.equals("AM") ? 0 : 1;
                spinnerAmPm.setSelection(amPmIndex);
            }


            String intervalString = String.valueOf(medication.getIntervalHrs());
            setSpinnerSelection(spinnerInterval, intervalString);


            setSpinnerSelection(spinnerFrequency, medication.getFrequency());


            selectedColor = medication.getColor();
            imgColorPicker.setBackgroundColor(selectedColor);

        } else {
            Toast.makeText(this, "Error al cargar medicamento.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().contains(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void showColorPickerDialog() {

        int[] availableColors = {
                Color.parseColor("#FF5722"), // Naranja
                Color.parseColor("#4CAF50"), // Verde
                Color.parseColor("#2196F3"), // Azul
                Color.parseColor("#9C27B0")  // Púrpura
        };

        int nextColor = availableColors[0];
        for (int i = 0; i < availableColors.length; i++) {
            if (selectedColor == availableColors[i]) {
                nextColor = availableColors[(i + 1) % availableColors.length];
                break;
            }
        }

        selectedColor = nextColor;
        imgColorPicker.setBackgroundColor(selectedColor);

        Toast.makeText(this, "Color seleccionado", Toast.LENGTH_SHORT).show();
    }

    private void showTimePickerDialog() {

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String am_pm = (hourOfDay < 12) ? "AM" : "PM";
                    int displayHour = hourOfDay % 12;
                    if (displayHour == 0) displayHour = 12;

                    String time = String.format(Locale.getDefault(), "%02d:%02d", displayHour, minuteOfHour);
                    txtInitialTime.setText(time);

                    int amPmIndex = am_pm.equals("AM") ? 0 : 1;
                    spinnerAmPm.setSelection(amPmIndex);
                }, hour, minute, false);
        timePickerDialog.show();
    }


    private void saveOrUpdateMedication() {
        String name = etMedicationName.getText().toString().trim();
        String dosesText = etDosesPerDay.getText().toString().trim();

        if (name.isEmpty() || dosesText.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos requeridos.", Toast.LENGTH_SHORT).show();
            return;
        }

        String time = txtInitialTime.getText().toString().trim();
        String amPm = spinnerAmPm.getSelectedItem().toString();
        String intervalText = spinnerInterval.getSelectedItem().toString();
        String frequency = spinnerFrequency.getSelectedItem().toString();

        int dosesPerDay = Integer.parseInt(dosesText);
        int intervalHrs = parseInterval(intervalText);

        Medication med = new Medication();
        med.setName(name);
        med.setColor(selectedColor);
        med.setDosesPerDay(dosesPerDay);
        med.setInitialTime(time + " " + amPm);
        med.setIntervalHrs(intervalHrs);
        med.setFrequency(frequency);

        long result = -1;

        if (medicationIdToEdit != -1) {

            med.setId(medicationIdToEdit);

            result = dbHelper.updateMedication(med);

            if (result > 0) { // updateMedication devuelve el número de filas actualizadas
                Toast.makeText(this, "Medicamento actualizado con éxito!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar el medicamento.", Toast.LENGTH_SHORT).show();
            }

        } else {
            //  Insertar
            result = dbHelper.addMedication(med);

            if (result > 0) {
                Toast.makeText(this, "Medicamento agregado con éxito!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar el medicamento.", Toast.LENGTH_SHORT).show();
            }
        }

        if (result > 0) {
            // Vuelve  pantalla principal
            Intent intent = new Intent(this, home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private int parseInterval(String intervalString) {
        try {

            String[] parts = intervalString.split(" ");
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 24;
        }
    }
}