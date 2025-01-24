package com.example.pmdmrrbtarea3.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmdmrrbtarea3.CapturedPokemonViewModel;
import com.example.pmdmrrbtarea3.Language;
import com.example.pmdmrrbtarea3.R;
import com.example.pmdmrrbtarea3.adapter.CapturedPokemonAdapter;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.Pokemon;

import java.util.ArrayList;

public class CapturedFragment extends Fragment {

    private CapturedPokemonAdapter capturedPokemonAdapter;
    private CapturedPokemonViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Language.applySavedLocale(requireContext()); // Asegúrate de aplicar el idioma correctamente

        // Configurar el listener para SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if ("switch_state".equals(key)) {
                // Obtener el nuevo estado del switch y actualizar la visibilidad del botón
                boolean switchState = sharedPreferences.getBoolean(key, false);
                updateLiberarButtonVisibility(switchState);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captured, container, false);

        // Configurar el RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPokemonCaptured);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        capturedPokemonAdapter = new CapturedPokemonAdapter(getContext(), new ArrayList<>(), pokemon -> {
            // Remover el Pokémon de la lista
            viewModel.getCapturedPokemons().getValue().remove(pokemon);
            capturedPokemonAdapter.notifyDataSetChanged();
        });
        recyclerView.setAdapter(capturedPokemonAdapter);

        // Obtener ViewModel y observar los datos
        viewModel = new ViewModelProvider(this).get(CapturedPokemonViewModel.class);
        viewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), pokemons -> {
            capturedPokemonAdapter.updateData(pokemons);
        });

        // Recuperar el estado del Switch desde SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        boolean isDeleteEnabled = preferences.getBoolean("switch_state", false);

        // Actualizar la visibilidad del botón de liberar basado en el estado del switch
        updateLiberarButtonVisibility(isDeleteEnabled);

        return view;
    }

    private void updateLiberarButtonVisibility(boolean isChecked) {
        // Verificar si la vista ya está inflada y obtener el botón de liberar
        View liberarButton = getView() != null ? getView().findViewById(R.id.btnLiberar) : null;

        // Si el botón no es null, establecer la visibilidad dependiendo del estado
        if (liberarButton != null) {
            liberarButton.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        }
    }

}
