package com.example.meditime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "meditime.db";

    private static final int DATABASE_VERSION = 3;


    private static final String TABLE_USERS = "usuarios";
    private static final String COL_ID = "id";
    private static final String COL_TIPO_DOC = "tipo_doc";
    private static final String COL_NUM_DOC = "num_doc";
    private static final String COL_NOMBRE = "nombre";
    private static final String COL_APELLIDOS = "apellidos";
    private static final String COL_FECHA = "fecha_nacimiento";
    private static final String COL_CORREO = "correo";
    private static final String COL_CONTRASENA = "contrasena";
    private static final String COL_TIPO_PERFIL = "tipo_perfil";
    private static final String COL_FOTO = "foto";
    private static final String COL_TELEFONO = "telefono";


    public static final String TABLE_MEDICATIONS = "medications_table";

    public static final String MED_COL_ID = "id";
    public static final String MED_COL_NAME = "name";
    public static final String MED_COL_DOSES = "doses_per_day";
    public static final String MED_COL_TIME = "initial_time";
    public static final String MED_COL_COLOR = "card_color";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TIPO_DOC + " TEXT, " +
                COL_NUM_DOC + " TEXT, " +
                COL_NOMBRE + " TEXT, " +
                COL_APELLIDOS + " TEXT, " +
                COL_FECHA + " TEXT, " +
                COL_CORREO + " TEXT, " +
                COL_CONTRASENA + " TEXT, " +
                COL_TIPO_PERFIL + " TEXT, " +
                COL_FOTO + " TEXT, " +
                COL_TELEFONO + " TEXT)";
        db.execSQL(createTableUsers);


        String createTableMedications = "CREATE TABLE " + TABLE_MEDICATIONS + " (" +
                MED_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MED_COL_NAME + " TEXT," +
                MED_COL_DOSES + " INTEGER," +
                MED_COL_TIME + " TEXT," +
                MED_COL_COLOR + " INTEGER)";
        db.execSQL(createTableMedications);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS); // AGREGADO
        onCreate(db);
    }


    public boolean insertarUsuario(String tipoDoc, String numDoc, String nombre, String apellidos,
                                   String fecha, String correo, String contrasena,
                                   String tipoPerfil, String fotoBase64, String telefono) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_TIPO_DOC, tipoDoc);
        valores.put(COL_NUM_DOC, numDoc);
        valores.put(COL_NOMBRE, nombre);
        valores.put(COL_APELLIDOS, apellidos);
        valores.put(COL_FECHA, fecha);
        valores.put(COL_CORREO, correo);
        valores.put(COL_CONTRASENA, contrasena);
        valores.put(COL_TIPO_PERFIL, tipoPerfil);
        valores.put(COL_FOTO, fotoBase64);
        valores.put(COL_TELEFONO, telefono);

        long resultado = db.insert(TABLE_USERS, null, valores);
        db.close();
        return resultado != -1;
    }


    public boolean usuarioExiste(String numDoc) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_NUM_DOC + "=?",
                new String[]{numDoc});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();
        return existe;
    }




    public int deleteMedication(long medicationId) {
        SQLiteDatabase db = this.getWritableDatabase();


        int rowsDeleted = db.delete(
                TABLE_MEDICATIONS,
                MED_COL_ID + " = ?",
                new String[]{String.valueOf(medicationId)}
        );

        db.close();
        return rowsDeleted;
    }


    public List<Medication> getAllMedications() {
        List<Medication> medicationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDICATIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {
                Medication medication = new Medication(
                        cursor.getLong(cursor.getColumnIndexOrThrow(MED_COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MED_COL_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MED_COL_DOSES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MED_COL_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MED_COL_COLOR))
                );
                medicationList.add(medication);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return medicationList;
    }

    public long addMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MED_COL_NAME, medication.getName());
        values.put(MED_COL_DOSES, medication.getDosesPerDay());
        values.put(MED_COL_TIME, medication.getInitialTime());
        values.put(MED_COL_COLOR, medication.getColor());

        long id = db.insert(TABLE_MEDICATIONS, null, values);
        db.close();
        return id;
    }
}