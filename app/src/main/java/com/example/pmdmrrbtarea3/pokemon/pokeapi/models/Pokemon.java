package com.example.pmdmrrbtarea3.pokemon.pokeapi.models;

import java.io.Serializable;
import java.util.List;

public class Pokemon implements Serializable {
    private String gameIndex;
    private String name;
    private String url;
    private int id;
    private List<String> types; // Lista de tipos (por ejemplo, ["fire", "flying"])
    private double height; // Altura
    private double weight; // Peso
    private List<String> abilities; // Lista de habilidades (por ejemplo, ["blaze", "solar-power"])
    private boolean isCaptured; // Agrega esta propiedad

    public Pokemon(String name, String url, int id, List<String> types, double height, double weight, List<String> abilities) {
        this.name = name;
        this.url = url;
        this.id = id;
        this.types = types;
        this.height = height;
        this.weight = weight;
        this.abilities = abilities;
    }
    // Constructor
    public Pokemon(String name, String url, double weight, double height) {
        this.name = name;
        this.url = url;
        this.weight = weight;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        // Extraer el ID desde la URL (asume que el ID está en la URL)
        String[] parts = url.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }
    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured(boolean captured) {
        isCaptured = captured;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }



}
