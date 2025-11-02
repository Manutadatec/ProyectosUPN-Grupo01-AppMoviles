package com.example.meditime;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import android.view.View;
import android.content.Intent;





import java.util.concurrent.TimeUnit;

public class Validacion extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText txtCodigo;
    private Button btnEnviarCodigo, btnVerificarCodigo;
    private String mVerificationId;
    private String numeroTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validacion);

        mAuth = FirebaseAuth.getInstance();

        numeroTelefono = getIntent().getStringExtra("telefono");

        txtCodigo = findViewById(R.id.txtcodigo);
        btnEnviarCodigo = findViewById(R.id.btnEnviarCodigo);
        btnVerificarCodigo = findViewById(R.id.btnVerificarCodigo);





        btnEnviarCodigo.setOnClickListener(v -> {
            if (TextUtils.isEmpty(numeroTelefono)) {
                Toast.makeText(Validacion.this, "El número de teléfono es inválido.", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("Validacion", "Número original recibido: '" + numeroTelefono + "'");
            numeroTelefono = numeroTelefono.trim().replaceAll("[^\\d+]", "");
            Log.d("Validacion", "Número después de limpieza: '" + numeroTelefono + "'");

            if (!numeroTelefono.startsWith("+51")) {
                numeroTelefono = "+51" + numeroTelefono;
            }
            Log.d("Validacion", "Número final para validar: '" + numeroTelefono + "'");

            if (!numeroTelefono.matches("^\\+51\\d{9}$")) {
                Toast.makeText(Validacion.this, "⚠️ Número inválido. Debe tener 9 dígitos después de +51", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("Validacion", "Número limpio para Firebase: " + numeroTelefono);

            sendVerificationCode(numeroTelefono);
        });

        btnVerificarCodigo.setOnClickListener(v -> {
            String codigo = txtCodigo.getText().toString().trim();
            if (TextUtils.isEmpty(codigo)) {
                Toast.makeText(Validacion.this, "Por favor ingresa el código de verificación", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyVerificationCode(codigo);
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        Log.d("Validacion", "Número de teléfono enviado: " + phoneNumber);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        Log.d("Validacion", "onVerificationCompleted:" + credential);
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.e("Validacion", "Error exacto: " + e.getMessage(), e);
                        Toast.makeText(Validacion.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        Log.d("Validacion", "onCodeSent:" + verificationId);
                        mVerificationId = verificationId;

                        findViewById(R.id.layoutCodigo).setVisibility(View.VISIBLE); // ← esta es la clave
                        btnVerificarCodigo.setVisibility(View.VISIBLE);

                        Toast.makeText(Validacion.this, "Código enviado. Revisa tu SMS.", Toast.LENGTH_SHORT).show();
                    }

                }
        );
    }

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Validacion", "signInWithCredential:success");
                        Toast.makeText(Validacion.this, "Autenticación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Validacion.this, home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        Log.w("Validacion", "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(Validacion.this, "Código incorrecto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


