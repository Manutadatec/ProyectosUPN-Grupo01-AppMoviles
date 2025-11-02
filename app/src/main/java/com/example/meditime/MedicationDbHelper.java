package com.example.meditime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.meditime.Medication;

import java.util.ArrayList;
import java.util.List;

public class MedicationDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MediTimeDB";
    private static final int DATABASE_VERSION = 2;

    // Tabla y Columnas
    private static final String TABLE_MEDICATIONS = "medications";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_COLOR = "color";
    private static final String KEY_DOSES_PER_DAY = "doses_per_day";
    private static final String KEY_INITIAL_TIME = "initial_time";
    private static final String KEY_INTERVAL_HRS = "interval_hrs";
    private static final String KEY_FREQUENCY = "frequency";

    public MedicationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEDICATIONS_TABLE = "CREATE TABLE " + TABLE_MEDICATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_COLOR + " INTEGER,"
                + KEY_DOSES_PER_DAY + " INTEGER,"
                + KEY_INITIAL_TIME + " TEXT,"
                + KEY_INTERVAL_HRS + " INTEGER,"
                + KEY_FREQUENCY + " TEXT" + ")";
        db.execSQL(CREATE_MEDICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        onCreate(db);
    }


    public long addMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, medication.getName());
        values.put(KEY_COLOR, medication.getColor());
        values.put(KEY_DOSES_PER_DAY, medication.getDosesPerDay());
        values.put(KEY_INITIAL_TIME, medication.getInitialTime());
        values.put(KEY_INTERVAL_HRS, medication.getIntervalHrs());
        values.put(KEY_FREQUENCY, medication.getFrequency());

        long id = db.insert(TABLE_MEDICATIONS, null, values);
        db.close();
        return id;
    }

    public List<Medication> getAllMedications() {
        List<Medication> medicationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDICATIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Medication med = new Medication();

                med.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
                med.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
                med.setColor(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COLOR)));
                med.setDosesPerDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DOSES_PER_DAY)));
                med.setInitialTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_INITIAL_TIME)));
                med.setIntervalHrs(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_INTERVAL_HRS)));
                med.setFrequency(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FREQUENCY)));

                medicationList.add(med);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return medicationList;
    }

    public int deleteMedication(long medicationId) {
        SQLiteDatabase db = this.getWritableDatabase();


        int rowsDeleted = db.delete(
                TABLE_MEDICATIONS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(medicationId)}
        );

        db.close();
        return rowsDeleted;
    }



    public Medication getMedication(long id) {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_MEDICATIONS,
                new String[]{KEY_ID, KEY_NAME, KEY_COLOR, KEY_DOSES_PER_DAY,
                        KEY_INITIAL_TIME, KEY_INTERVAL_HRS, KEY_FREQUENCY},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            Medication medication = new Medication(
                    cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DOSES_PER_DAY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_INITIAL_TIME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COLOR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_INTERVAL_HRS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_FREQUENCY))
            );
            cursor.close();
            db.close();
            return medication;
        }

        if (cursor != null) cursor.close();
        db.close();
        return null; // No se encontró
    }

    public int updateMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(KEY_NAME, medication.getName());
        values.put(KEY_COLOR, medication.getColor());
        values.put(KEY_DOSES_PER_DAY, medication.getDosesPerDay());
        values.put(KEY_INITIAL_TIME, medication.getInitialTime());
        values.put(KEY_INTERVAL_HRS, medication.getIntervalHrs());
        values.put(KEY_FREQUENCY, medication.getFrequency());


        int rowsUpdated = db.update(TABLE_MEDICATIONS,
                values,
                KEY_ID + " = ?",
                new String[]{String.valueOf(medication.getId())});

        db.close();
        return rowsUpdated;
    }

}


