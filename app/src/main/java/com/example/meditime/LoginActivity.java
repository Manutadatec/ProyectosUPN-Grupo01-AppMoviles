package com.example.meditime;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.text.InputType;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText txtDniCorreo, txtContrasenaLogin;
    Button btnIniciarSesion;

    ImageView icOjo;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        txtDniCorreo = findViewById(R.id.txttelefono);
        txtContrasenaLogin = findViewById(R.id.txtcontra);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        icOjo = findViewById(R.id.ic_ojo);


        db = new DatabaseHelper(this);

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        icOjo.setOnClickListener(v -> {
            if (txtContrasenaLogin.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                txtContrasenaLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                icOjo.setImageResource(R.drawable.ic_eye);
            } else {
                txtContrasenaLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                icOjo.setImageResource(R.drawable.ic_eye);
            }
            txtContrasenaLogin.setSelection(txtContrasenaLogin.getText().length());
        });

    }

    private void iniciarSesion() {
        String usuario = txtDniCorreo.getText().toString().trim();
        String contrasena = txtContrasenaLogin.getText().toString().trim();



        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }


        if (verificarUsuario(usuario, contrasena)) {
            Toast.makeText(this, "✅ Bienvenido a MediTime", Toast.LENGTH_SHORT).show();


            SQLiteDatabase dbReadable = db.getReadableDatabase();
            Cursor cursor = dbReadable.rawQuery(
                    "SELECT telefono FROM usuarios WHERE (num_doc=? OR correo=?) AND contrasena=?",
                    new String[]{usuario, usuario, contrasena}
            );

            String numeroTelefono = null;
            if (cursor.moveToFirst()) {
                numeroTelefono = cursor.getString(0);
            }
            cursor.close();
            dbReadable.close();

            if (numeroTelefono != null && !numeroTelefono.isEmpty()) {
                Intent intent = new Intent(this, Validacion.class);
                intent.putExtra("telefono", numeroTelefono);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "No se encontró el número de teléfono", Toast.LENGTH_SHORT).show();
            }
        }

    }



    private boolean verificarUsuario(String dniCorreo, String contrasena) {
        boolean existe = false;
        String query = "SELECT * FROM usuarios WHERE (num_doc=? OR correo=?) AND contrasena=?";
        SQLiteDatabase dbReadable = db.getReadableDatabase();
        Cursor cursor = dbReadable.rawQuery(query, new String[]{dniCorreo, dniCorreo, contrasena});
        if (cursor.moveToFirst()) {
            existe = true;
        }
        cursor.close();
        dbReadable.close();
        return existe;
    }
}


