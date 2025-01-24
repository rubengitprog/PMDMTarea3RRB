package com.example.pmdmrrbtarea3;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.Pokemon;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CapturedPokemonViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Pokemon>> capturedPokemons = new MutableLiveData<>();

    public CapturedPokemonViewModel() {
        fetchCapturedPokemons();
    }

    public LiveData<ArrayList<Pokemon>> getCapturedPokemons() {
        return capturedPokemons;
    }

    private void fetchCapturedPokemons() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            capturedPokemons.setValue(new ArrayList<>()); // Si no hay usuario, lista vacía
            return;
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference capturedPokemonsRef = db.collection("users")
                .document(userId)
                .collection("captured_pokemons");

        capturedPokemonsRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e("fetchCapturedPokemons", "Error al escuchar cambios en Firestore", e);
                capturedPokemons.setValue(new ArrayList<>()); // Si hay error, lista vacía
                return;
            }

            if (snapshots != null) {
                ArrayList<Pokemon> pokemonList = new ArrayList<>();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : snapshots) {
                    String name = doc.getString("name");
                    String url = doc.getString("url");
                    double weight = doc.getDouble("weight") != null ? doc.getDouble("weight") : 0;
                    double height = doc.getDouble("height") != null ? doc.getDouble("height") : 0;

                    // Recuperar habilidades como lista
                    List<String> abilities = (List<String>) doc.get("abilities");
                    if (abilities == null) abilities = new ArrayList<>();

                    // Recuperar tipos como lista
                    List<String> types = (List<String>) doc.get("types");
                    if (types == null) types = new ArrayList<>();

                    // Crear objeto Pokemon
                    Pokemon pokemon = new Pokemon(name, url, 0, types, height, weight, abilities);

                    pokemonList.add(pokemon);
                }
                capturedPokemons.setValue(pokemonList); // Actualizar la lista de Pokémon capturados
            }
        });
    }

}
