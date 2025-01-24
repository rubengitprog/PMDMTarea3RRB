package com.example.pmdmrrbtarea3.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.example.pmdmrrbtarea3.Language;
import com.example.pmdmrrbtarea3.R;
import com.example.pmdmrrbtarea3.ToastUtils;
import com.example.pmdmrrbtarea3.activity.FireBaseLoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Cargar las preferencias desde el archivo XML
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Escuchar cambios en las preferencias
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Establecer el listener para el botón de cerrar sesión
        Preference logoutButton = findPreference("logout_button");
        if (logoutButton != null) {
            logoutButton.setOnPreferenceClickListener(preference -> {
                onLogoutButtonClicked(); // Llamar al método para cerrar sesión
                return true; // Indica que el clic ha sido manejado
            });
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("language")) {
            // Cambiar idioma si se selecciona un nuevo idioma
            String language = sharedPreferences.getString("language", "es");
            Language.setLocale(requireContext(), language);

            // Recargar actividad para aplicar cambios de idioma
            requireActivity().recreate();
        }

        if (key.equals("switch_delete")) {
            boolean switchState = sharedPreferences.getBoolean("switch_delete", false);
            // Guardar el estado del switch en las preferencias
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("switch_delete", switchState); // Usa la misma clave "switch_delete"
            editor.apply(); // Aplica los cambios
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Desregistrar el listener para evitar fugas de memoria
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onLogoutButtonClicked() {
        // Crear un AlertDialog de confirmación
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.confirmacionCerrarSesion))  // Título del diálogo
                .setMessage(getString(R.string.pulsa_para_cerrar_sesi_n))  // Mensaje del diálogo
                .setPositiveButton(getString(R.string.si), (dialog, which) -> {
                    // Si el usuario confirma, cerrar sesión
                    FirebaseAuth.getInstance().signOut();

                    // Mostrar un mensaje de confirmación
                    ToastUtils.showCustomToast(requireContext(), getString(R.string.sesion_cerrada));

                    // Redirigir al Login
                    Intent intent = new Intent(requireContext(), FireBaseLoginActivity.class); // Usamos requireContext() aquí
                    startActivity(intent);

                    // Finalizar la actividad actual para evitar que el usuario regrese a esta pantalla presionando atrás
                    requireActivity().finish(); // Utiliza requireActivity() para finalizar la actividad
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    // Si el usuario cancela, solo cerrar el diálogo
                    dialog.dismiss();
                })
                .show();  // Mostrar el diálogo
    }
}
