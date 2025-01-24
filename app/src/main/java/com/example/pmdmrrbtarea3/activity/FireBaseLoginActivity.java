package com.example.pmdmrrbtarea3.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pmdmrrbtarea3.Language;
import com.example.pmdmrrbtarea3.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class FireBaseLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // Configurar idioma antes de inicializar la vista
        Language.applySavedLocale(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Si ya hay un usuario autenticado, redirigir a la actividad principal
            navigateToPokedex();
        } else {
            // Iniciar el proceso de autenticación
            startSignIn();
        }
    }

    private void startSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                // Logo personalizado
                .setLogo(R.drawable.pokedexlogo)
                // Tema personalizado
                .setTheme(R.style.FirebaseLoginTheme)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();

        if (result.getResultCode() == RESULT_OK) {
            // Inicio de sesión exitoso
            navigateToPokedex();
        } else {
            // Manejar errores o cancelación del inicio de sesión
            if (response != null && response.getError() != null) {
                String errorMessage = R.string.errorInicioSesion + response.getError().getMessage();
                Log.e("FirebaseAuth", errorMessage);
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.sesionCancelada, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void navigateToPokedex() {
        Intent intent = new Intent(this, PokedexActivity.class);
        startActivity(intent);
        // Finaliza esta actividad para que no se pueda volver con el botón Atrás
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // Configurar idioma antes de adjuntar el contexto
        super.attachBaseContext(Language.updateBaseContextLocale(newBase));
    }
}
