package com.example.pmdmrrbtarea3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pmdmrrbtarea3.pokemon.pokeapi.PokeApi;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.Pokemon;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.PokemonResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Pokemon>> pokemonsLiveData = new MutableLiveData<>();
    private final ArrayList<Pokemon> pokemonList = new ArrayList<>();
    private int offset = 0;
    private final int pageSize = 20;
    private boolean isLoading = false;

    public LiveData<ArrayList<Pokemon>> getPokemons() {
        return pokemonsLiveData;
    }

    public void loadMorePokemons() {
        if (isLoading) return;
        isLoading = true;

        PokeApi service = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PokeApi.class);

        Call<PokemonResponse> call = service.obtenerPokemon(pageSize, offset);
        call.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.body() != null && response.body().getResults() != null) {
                    ArrayList<Pokemon> newPokemons = response.body().getResults();
                    pokemonList.addAll(newPokemons);
                    pokemonsLiveData.postValue(pokemonList);
                    offset += pageSize;
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                isLoading = false;
            }
        });
    }
}

