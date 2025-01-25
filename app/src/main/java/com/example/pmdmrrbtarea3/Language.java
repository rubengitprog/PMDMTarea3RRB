package com.example.pmdmrrbtarea3;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class Language {
    /**
     * Aplica el idioma guardado en las preferencias compartidas.
     *
     * @param context El contexto de la aplicación o actividad.
     */
    public static void applySavedLocale(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String savedLanguage = preferences.getString("selected_language", Locale.getDefault().getLanguage());
        setLocale(context, savedLanguage);
    }

    public static void applyLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static Context updateBaseContextLocale(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String languageCode = preferences.getString("selected_language", Locale.getDefault().getLanguage());
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }

    public static void setLocale(Context context, String languageCode) {
        // Guardar el idioma seleccionado
        SharedPreferences preferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        preferences.edit().putString("selected_language", languageCode).apply();

        // Cambiar la configuración del idioma
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

}