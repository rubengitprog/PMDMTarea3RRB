package com.example.pmdmrrbtarea3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {

    // Método para mostrar el Toast personalizado
    public static void showCustomToast(Context context, String message) {
        // Infla el diseño personalizado
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_custom, null);

        // Configura el mensaje y la imagen
        ImageView toastIcon = layout.findViewById(R.id.toast_icon);
        TextView toastMessage = layout.findViewById(R.id.toast_message);

        toastIcon.setImageResource(R.drawable.pokedexlogo); // Cambia al ícono deseado
        toastMessage.setText(message);

        // Crea y muestra el Toast
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}

