package com.example.pmdmrrbtarea3.pokemon.pokeapi;

import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.PokemonDetail;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.PokemonResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeApi {

        @GET("pokemon")
        Call<PokemonResponse> obtenerPokemon(@Query("limit") int limit, @Query("offset") int offset);
        @GET("pokemon/{id}")
        Call<PokemonDetail> getPokemonDetail(@Path("id") int id);
}
