package com.example.pmdmrrbtarea3.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pmdmrrbtarea3.R;
import com.example.pmdmrrbtarea3.ToastUtils;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.PokeApi;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.Pokemon;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.PokemonDetail;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.PokemonResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoCapturedPokemonAdapter extends RecyclerView.Adapter<NoCapturedPokemonAdapter.PokemonViewHolder> {

    private ArrayList<Pokemon> pokemonList;
    private final Context context;
    private final PokemonCaptureListener captureListener;
    private boolean isLoading = false;
    private int offset = 0;  // Se empieza en 0
    private final int pageSize = 20; // Tamaño de cada página

    public boolean isLoading() {
        return isLoading;
    }

    public void updatePokemons(ArrayList<Pokemon> newPokemons) {
        pokemonList = newPokemons; // Reemplaza la lista anterior
        notifyDataSetChanged();
    }


    // Interfaz para notificar a la actividad/fragmento cuando un Pokémon ha sido capturado
    public interface PokemonCaptureListener {
        void onPokemonCaptured(Pokemon pokemon);
    }

    public NoCapturedPokemonAdapter(Context context, ArrayList<Pokemon> pokemonList, PokemonCaptureListener captureListener) {
        this.context = context;
        this.pokemonList = pokemonList;
        this.captureListener = captureListener;
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = pokemonList.get(position);

        // Muestra el nombre y la imagen
        holder.pokemonName.setText(capitalizeFirstLetter(pokemon.getName()));
        int pokemonId = pokemon.getId();

        // Cargar la imagen del Pokémon
        loadPokemonImage(pokemonId, holder.pokemonImage);

        // Si el Pokémon está capturado, ponerlo en gris y deshabilitar el clic
        if (pokemon.isCaptured()) {
            holder.itemView.setAlpha(0.5f); // Reduce la opacidad
            holder.itemView.setClickable(false); // Deshabilita el clic
        } else {
            holder.itemView.setAlpha(1f); // Restaura la opacidad
            holder.itemView.setClickable(true); // Habilita el clic
            holder.itemView.setOnClickListener(v -> capturePokemon(pokemon));
        }

        // Cargar más Pokémon cuando se llega al final de la lista
        if (position == pokemonList.size() - 1 && !isLoading) {
            loadMorePokemons();
        }
    }

    /**
     * Capitaliza la primera letra de una cadena.
     */
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public void addPokemons(ArrayList<Pokemon> newPokemons) {
        int startIndex = pokemonList.size();
        pokemonList.addAll(newPokemons);
        notifyItemRangeInserted(startIndex, newPokemons.size());
    }

    public void loadMorePokemons() {
        isLoading = true;

        // Llamada a la API para cargar más Pokémon
        PokeApi service = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PokeApi.class);

        Call<PokemonResponse> pokemonResponseCall = service.obtenerPokemon(pageSize, offset);
        pokemonResponseCall.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.body() != null && response.body().getResults() != null) {
                    ArrayList<Pokemon> newPokemons = response.body().getResults();
                    addPokemons(newPokemons);
                    // Incrementa el offset
                    offset += pageSize;
                } else {
                    Log.e("NoCapturedPokemonAdapter", String.valueOf(R.string.errorCapturarPokemon));
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Log.e("NoCapturedPokemonAdapter", String.valueOf(R.string.errorCargarPokemon) + t.getMessage());
                isLoading = false;
            }
        });
    }

    private void loadPokemonImage(int pokemonId, ImageView imageView) {
        String imageUrl = getPokemonImageUrl(pokemonId);

        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        // Imagen mientras se carga cada item pokemon
                        .placeholder(R.drawable.loading)
                        // Imagen de error si no se carga el item pokemon
                        .error(R.drawable.error)
                        // Recorta la imagen para centrarla
                        .centerCrop())
                .into(imageView);
    }

    private String getPokemonImageUrl(int pokemonId) {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonId + ".png";
    }

    private void capturePokemon(Pokemon pokemon) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(context, "Usuario no autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        // Llamada a la API para obtener detalles del Pokémon
        PokeApi service = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PokeApi.class);

        Call<PokemonDetail> call = service.getPokemonDetail(pokemon.getId());
        call.enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(Call<PokemonDetail> call, Response<PokemonDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonDetail details = response.body();

                    // Marcar el Pokémon como capturado
                    pokemon.setCaptured(true);
                    notifyDataSetChanged(); // Actualiza la UI para reflejar el cambio

                    // Crear el mapa de datos con información adicional
                    Map<String, Object> pokemonData = new HashMap<>();
                    pokemonData.put("name", pokemon.getName());
                    pokemonData.put("url", pokemon.getUrl());
                    pokemonData.put("id", pokemon.getId());
                    pokemonData.put("weight", details.getWeight());
                    pokemonData.put("height", details.getHeight());
// Extraer los nombres de los tipos y agregarlos al mapa
                    List<String> typeNames = new ArrayList<>();
                    for (PokemonDetail.Type type : details.getTypes()) {
                        typeNames.add(type.getType().getName()); // Extrae el nombre del tipo
                    }
                    pokemonData.put("types", typeNames); // Guarda la lista de nombres de tipos
                    Log.d("PokemonDetail", "Types: " + typeNames);

// Extraer las habilidades y agregarlas al mapa
                    List<String> abilityNames = new ArrayList<>();
                    for (PokemonDetail.Ability ability : details.getAbilities()) {
                        abilityNames.add(ability.getAbility().getName()); // Extrae el nombre de la habilidad
                    }
                    pokemonData.put("abilities", abilityNames); // Guarda la lista de nombres de tipos
                    Log.d("PokemonDetail", "Abilities: " + abilityNames);
                    db.collection("users").document(userId).collection("captured_pokemons")
                            .document(pokemon.getName())
                            .set(pokemonData)
                            .addOnSuccessListener(aVoid -> {
                                ToastUtils.showCustomToast(context, capitalizeFirstLetter(pokemon.getName()) + " " +context.getString(R.string.capturado));
                                if (captureListener != null) {
                                    captureListener.onPokemonCaptured(pokemon);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Error al capturar el Pokémon.", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onFailure(Call<PokemonDetail> call, Throwable t) {
                Toast.makeText(context, "Error al obtener detalles del Pokémon.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        ImageView pokemonImage;
        TextView pokemonName;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            pokemonImage = itemView.findViewById(R.id.ivPokemonImage);
            pokemonName = itemView.findViewById(R.id.tvName);
        }

    }
}
