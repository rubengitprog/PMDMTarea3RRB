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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pmdmrrbtarea3.Language;
import com.example.pmdmrrbtarea3.R;
import com.example.pmdmrrbtarea3.activity.PokedexActivity;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.Pokemon;

import java.util.List;

public class PokemonDetailFragment extends Fragment {

    private static final String ARG_POKEMON = "pokemon";

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
        View view = inflater.inflate(R.layout.fragment_pokemondetail, container, false);
        Language.applySavedLocale(requireContext());
        // Obtener el Pokémon desde los argumentos
        Pokemon pokemon = (Pokemon) getArguments().getSerializable(ARG_POKEMON);

        // Referencias a las vistas del diseño
        TextView name = view.findViewById(R.id.tvPokemonName);
        ImageView image = view.findViewById(R.id.ivPokemonImage);
        TextView weight = view.findViewById(R.id.tvPokemonWeight);
        TextView height = view.findViewById(R.id.tvPokemonHeight);
        TextView abilities = view.findViewById(R.id.tvPokemonAbilities);
        TextView types = view.findViewById(R.id.tvPokemonTypes);

        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Mostrar los datos del Pokémon
        if (pokemon != null) {
            // Nombre, peso y altura
            name.setText(pokemon.getName());
            weight.setText(pokemon.getWeight() > 0 ? getString(R.string.peso) + pokemon.getWeight() + " kg" : "Peso: N/A");
            height.setText(pokemon.getHeight() > 0 ? getString(R.string.altura) + pokemon.getHeight() + " m" : "Altura: N/A");

            // Habilidades
            abilities.setText(formatList(getString(R.string.habilidades), pokemon.getAbilities()));

            // Tipos
            types.setText(formatList(getString(R.string.tipos), pokemon.getTypes()));

            // Imagen del Pokémon
            Glide.with(requireContext())
                    .load(getPokemonImageUrl(pokemon.getUrl()))
                    .into(image);
        }

        return view;
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
