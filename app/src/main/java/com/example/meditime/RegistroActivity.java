package com.example.meditime;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class RegistroActivity extends AppCompatActivity {

    EditText txtFechaNacimiento, txtContrasena, txtRepetirContrasena, txtNumeroDni ;
    ImageView icOjo1, icOjo2, btnVolver, imgPerfil;
    Spinner spinnerDni;
    Button btnRegistrar;
    boolean verContrasena1 = false;
    boolean verContrasena2 = false;


    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 200;
    private static final int REQUEST_PERMISSION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento);
        txtContrasena = findViewById(R.id.txtContrasena);
        txtRepetirContrasena = findViewById(R.id.txtRepetirContrasena);
        txtNumeroDni = findViewById(R.id.txtNumeroDni);
        icOjo1 = findViewById(R.id.ic_ojo1);
        icOjo2 = findViewById(R.id.ic_ojo2);
        btnVolver = findViewById(R.id.btnVolver);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        spinnerDni = findViewById(R.id.spinnerDni);
        imgPerfil = findViewById(R.id.imgPerfil);

        TextView txtAgregarFoto = findViewById(R.id.txtAgregarFoto);


        String[] tiposDocumento = {"DNI", "Carnet de Extranjería", "Pasaporte"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, tiposDocumento);
        spinnerDni.setAdapter(adapter);


        txtFechaNacimiento.setOnClickListener(v -> mostrarCalendario());

        icOjo1.setOnClickListener(v -> alternarContrasena(txtContrasena, icOjo1, true));
        icOjo2.setOnClickListener(v -> alternarContrasena(txtRepetirContrasena, icOjo2, false));


        btnVolver.setOnClickListener(v -> onBackPressed());


        txtAgregarFoto.setOnClickListener(v -> mostrarOpcionesFoto());


        btnRegistrar.setOnClickListener(v -> validarYRegistrar());
    }



    private void mostrarCalendario() {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialogoFecha = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, (month + 1), year);
                    txtFechaNacimiento.setText(fechaSeleccionada);
                },
                año, mes, dia
        );
        dialogoFecha.show();
    }

    private void alternarContrasena(EditText campo, ImageView icono, boolean esPrimera) {
        boolean mostrando;

        if (esPrimera) {
            verContrasena1 = !verContrasena1;
            mostrando = verContrasena1;
        } else {
            verContrasena2 = !verContrasena2;
            mostrando = verContrasena2;
        }

        if (mostrando) {
            campo.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icono.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_blue_light));
        } else {
            campo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icono.setColorFilter(ContextCompat.getColor(this, android.R.color.black));
        }

        campo.setSelection(campo.getText().length());
    }



    private void mostrarOpcionesFoto() {
        String[] opciones = {"Tomar foto", "Elegir de galería"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        verificarPermisosYAbrirCamara();
                    } else {
                        verificarPermisosYAbrirGaleria();
                    }
                })
                .show();
    }

    private void verificarPermisosYAbrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        } else {
            abrirCamara();
        }
    }

    private void verificarPermisosYAbrirGaleria() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            abrirGaleria();
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                Bitmap foto = (Bitmap) data.getExtras().get("data");
                imgPerfil.setImageBitmap(recortarEnCirculo(foto));
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                Uri imagenSeleccionada = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenSeleccionada);
                    imgPerfil.setImageBitmap(recortarEnCirculo(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap recortarEnCirculo(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        BitmapShader shader = new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);

        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);
        return output;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido ✅", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso denegado ❌", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void validarYRegistrar() {
        DatabaseHelper db = new DatabaseHelper(this);

        String tipoDocumento = spinnerDni.getSelectedItem().toString();
        String numeroDocumento = txtNumeroDni.getText().toString().trim();
        String fecha = txtFechaNacimiento.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();
        String repetirContrasena = txtRepetirContrasena.getText().toString().trim();

        EditText txtNombre = findViewById(R.id.txtNombre);
        EditText txtApellidos = findViewById(R.id.txtApellidos);
        EditText txtCorreo = findViewById(R.id.txtCorreo);

        RadioGroup grupoPerfil = findViewById(R.id.grupoPerfil);
        EditText txtTelefono = findViewById(R.id.txtTelefono);

        String nombre = txtNombre.getText().toString().trim();
        String apellidos = txtApellidos.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();

        int idSeleccionado = grupoPerfil.getCheckedRadioButtonId();
        String tipoPerfil = (idSeleccionado == R.id.rbDoctor) ? "Doctor" : "Paciente";

        if (nombre.isEmpty() || apellidos.isEmpty() || numeroDocumento.isEmpty() ||
                correo.isEmpty() || fecha.isEmpty() || contrasena.isEmpty() || repetirContrasena.isEmpty() ||  telefono.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        telefono = telefono.trim().replaceAll("[^\\d+]", "");

        if (!telefono.startsWith("+51")) {
            telefono = "+51" + telefono;
        }

        if (!telefono.matches("^\\+51\\d{9}$")) {
            Toast.makeText(this, "⚠️ Número de teléfono inválido. Debe tener 9 dígitos después de +51", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!contrasena.equals(repetirContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.usuarioExiste(numeroDocumento)) {
            Toast.makeText(this, "⚠️ Ya existe un usuario con ese número de documento", Toast.LENGTH_SHORT).show();
            return;
        }


        String fotoBase64 = "";
        imgPerfil.setDrawingCacheEnabled(true);
        imgPerfil.buildDrawingCache();
        Bitmap bitmap = imgPerfil.getDrawingCache();
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imagenBytes = baos.toByteArray();
            fotoBase64 = android.util.Base64.encodeToString(imagenBytes, android.util.Base64.DEFAULT);
        }

        boolean insertado = db.insertarUsuario(tipoDocumento, numeroDocumento, nombre, apellidos,
                fecha, correo, contrasena, tipoPerfil, fotoBase64 , telefono);

        if (insertado) {
            Toast.makeText(this, "✅ Registro exitoso ", Toast.LENGTH_LONG).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "❌ Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }


    private void limpiarCampos() {
        txtNumeroDni.setText("");
        txtFechaNacimiento.setText("");
        txtContrasena.setText("");
        txtRepetirContrasena.setText("");
        EditText txtNombre = findViewById(R.id.txtNombre);
        EditText txtApellidos = findViewById(R.id.txtApellidos);
        EditText txtCorreo = findViewById(R.id.txtCorreo);
        EditText txtTelefono = findViewById(R.id.txtTelefono);
        txtNombre.setText("");
        txtApellidos.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
    }


}
