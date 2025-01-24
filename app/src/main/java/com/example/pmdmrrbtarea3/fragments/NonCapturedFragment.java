package com.example.pmdmrrbtarea3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmdmrrbtarea3.Language;
import com.example.pmdmrrbtarea3.PokemonViewModel;
import com.example.pmdmrrbtarea3.R;
import com.example.pmdmrrbtarea3.adapter.NoCapturedPokemonAdapter;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.Pokemon;

import java.util.ArrayList;

public class NonCapturedFragment extends Fragment implements NoCapturedPokemonAdapter.PokemonCaptureListener {

    private RecyclerView recyclerView;
    private NoCapturedPokemonAdapter pokemonAdapter;
    private PokemonViewModel pokemonViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nocaptured, container, false);
        Language.applySavedLocale(requireContext());
        recyclerView = view.findViewById(R.id.recyclerViewPokemon);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pokemonAdapter = new NoCapturedPokemonAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(pokemonAdapter);

        pokemonViewModel = new ViewModelProvider(this).get(PokemonViewModel.class);

        // Observar cambios en LiveData
        pokemonViewModel.getPokemons().observe(getViewLifecycleOwner(), pokemons -> {
            pokemonAdapter.updatePokemons(pokemons);
        });

        // Cargar datos iniciales
        pokemonViewModel.loadMorePokemons();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (dy > 0 && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    pokemonViewModel.loadMorePokemons();
                }
            }
        });

        return view;
    }

    @Override
    public void onPokemonCaptured(Pokemon pokemon) {
        pokemonAdapter.notifyDataSetChanged();
    }
}