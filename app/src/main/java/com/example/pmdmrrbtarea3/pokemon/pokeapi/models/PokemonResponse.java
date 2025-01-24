package com.example.pmdmrrbtarea3.pokemon.pokeapi.models;

import java.util.ArrayList;

public class PokemonResponse {
    private ArrayList<Pokemon> results;
    private String next;

    public ArrayList<Pokemon> getResults() {
        return results;
    }

    public String getNext() {
        return next;
    }
}
