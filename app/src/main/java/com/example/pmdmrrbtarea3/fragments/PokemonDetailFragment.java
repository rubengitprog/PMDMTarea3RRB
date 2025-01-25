package com.example.pmdmrrbtarea3.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.example.pmdmrrbtarea3.Language;
import com.example.pmdmrrbtarea3.R;
import com.example.pmdmrrbtarea3.activity.PokedexActivity;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.Pokemon;
import com.example.pmdmrrbtarea3.databinding.FragmentPokemondetailBinding;  // Importa la clase de ViewBinding generada

import java.util.List;

public class PokemonDetailFragment extends Fragment {

    private static final String ARG_POKEMON = "pokemon";
    private FragmentPokemondetailBinding binding;  // Variable de binding

    /**
     * Método estático para crear una nueva instancia del fragmento con el Pokémon seleccionado.
     */
    public static PokemonDetailFragment newInstance(Pokemon pokemon) {
        PokemonDetailFragment fragment = new PokemonDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POKEMON, pokemon);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Usa el ViewBinding para inflar el layout
        binding = FragmentPokemondetailBinding.inflate(inflater, container, false);
        Language.applySavedLocale(requireContext());

        // Obtener el Pokémon desde los argumentos
        Pokemon pokemon = (Pokemon) getArguments().getSerializable(ARG_POKEMON);

        // Mostrar los datos del Pokémon
        if (pokemon != null) {
            // Nombre, peso y altura
            binding.tvPokemonName.setText(pokemon.getName());
            binding.tvPokemonWeight.setText(pokemon.getWeight() > 0 ? getString(R.string.peso) + pokemon.getWeight() + " kg" : "Peso: N/A");
            binding.tvPokemonHeight.setText(pokemon.getHeight() > 0 ? getString(R.string.altura) + pokemon.getHeight() + " m" : "Altura: N/A");

            // Habilidades
            binding.tvPokemonAbilities.setText(formatList(getString(R.string.habilidades), pokemon.getAbilities()));

            // Tipos
            binding.tvPokemonTypes.setText(formatList(getString(R.string.tipos), pokemon.getTypes()));

            // Imagen del Pokémon
            Glide.with(requireContext())
                    .load(getPokemonImageUrl(pokemon.getUrl()))
                    .into(binding.ivPokemonImage);
        }

        // Configura el botón volver
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return binding.getRoot();  // Devuelve la vista raíz del binding
    }

    /**
     * Construye una URL para obtener la imagen del Pokémon desde la PokeAPI.
     */
    private String getPokemonImageUrl(String url) {
        String[] parts = url.split("/");
        String id = parts[parts.length - 1].isEmpty() ? parts[parts.length - 2] : parts[parts.length - 1];
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
    }

    /**
     * Formatea una lista de elementos en una cadena legible.
     * Si la lista es nula o vacía, devuelve un mensaje "N/A".
     */
    private String formatList(String label, List<String> items) {
        if (items != null && !items.isEmpty()) {
            return label + ": " + String.join(", ", items);
        } else {
            return label + ": N/A";
        }
    }

    /**
     * Al destruir la vista del fragmento, restaura la visibilidad de la vista principal en la actividad.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Limpiar el binding para evitar fugas de memoria
        binding = null;

        if (getActivity() instanceof PokedexActivity) {
            PokedexActivity activity = (PokedexActivity) getActivity();

            // Restaurar la visibilidad del contenido principal
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
            if (viewPager != null) viewPager.setVisibility(View.VISIBLE);

            FrameLayout fragmentContainer = activity.findViewById(R.id.fragment_container);
            if (fragmentContainer != null) fragmentContainer.setVisibility(View.GONE);
        }
    }
}
