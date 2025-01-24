package com.example.pmdmrrbtarea3.pokemon.pokeapi.models;

public class Ability {
    private String name;

    public Ability(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name; // Para facilitar la representación en texto
    }
}
