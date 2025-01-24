package com.example.pmdmrrbtarea3.pokemon.pokeapi.models;

import java.util.List;

public class PokemonDetail {
    private int weight;
    private int height;
    private List<Type> types;
    private List<Ability> abilities;

    // Getters y setters
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public List<Type> getTypes() { return types; }
    public void setTypes(List<Type> types) { this.types = types; }

    public List<Ability> getAbilities() { return abilities; }
    public void setAbilities(List<Ability> abilities) { this.abilities = abilities; }

    public static class Type {
        private TypeDetail type;

        public TypeDetail getType() { return type; }
        public void setType(TypeDetail type) { this.type = type; }

        public static class TypeDetail {
            private String name;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }

    public static class Ability {
        private AbilityDetail ability;

        public AbilityDetail getAbility() { return ability; }
        public void setAbility(AbilityDetail ability) { this.ability = ability; }

        public static class AbilityDetail {
            private String name;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }
}

