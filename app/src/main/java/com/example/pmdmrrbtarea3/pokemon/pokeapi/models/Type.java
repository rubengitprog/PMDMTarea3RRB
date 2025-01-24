package com.example.pmdmrrbtarea3.pokemon.pokeapi.models;

public class Type {
    private String name;

    public Type(String name) {
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
