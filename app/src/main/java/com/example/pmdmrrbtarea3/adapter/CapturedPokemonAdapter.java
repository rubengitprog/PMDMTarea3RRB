package com.example.pmdmrrbtarea3.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.pmdmrrbtarea3.R;
import com.example.pmdmrrbtarea3.activity.PokedexActivity;
import com.example.pmdmrrbtarea3.databinding.ItemPokemonCapturedBinding;
import com.example.pmdmrrbtarea3.fragments.PokemonDetailFragment;
import com.example.pmdmrrbtarea3.pokemon.pokeapi.models.Pokemon;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CapturedPokemonAdapter extends RecyclerView.Adapter<CapturedPokemonAdapter.PokemonViewHolder> {

    private final ArrayList<Pokemon> capturedPokemonList;
    private final Context context;
    private PokemonReleaseListener releaseListener;
    private boolean isDeleteEnabled;

    public CapturedPokemonAdapter(Context context, ArrayList<Pokemon> capturedPokemonList, PokemonReleaseListener releaseListener) {
        this.context = context;
        this.capturedPokemonList = capturedPokemonList;
        this.releaseListener = releaseListener;
        this.isDeleteEnabled = false; // Inicializamos con el valor predeterminado
    }

    // Método para actualizar el estado del Switch
    public void setDeleteEnabled(boolean isDeleteEnabled) {
        this.isDeleteEnabled = isDeleteEnabled;

        // Usamos un Handler para que notifyDataSetChanged se ejecute fuera del ciclo de layout
        new Handler(Looper.getMainLooper()).post(() -> {
            notifyDataSetChanged(); // Notificar los cambios al RecyclerView
        });
    }

    public void updateData(ArrayList<Pokemon> newPokemonList) {
        this.capturedPokemonList.clear();
        this.capturedPokemonList.addAll(newPokemonList);
        notifyDataSetChanged();
    }

    public interface PokemonReleaseListener {
        void onPokemonReleased(Pokemon pokemon);
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPokemonCapturedBinding binding = ItemPokemonCapturedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PokemonViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = capturedPokemonList.get(position);

        // Recuperar el estado del Switch desde SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("PokedexPreferences", Context.MODE_PRIVATE);
        boolean isDeleteEnabledFromPrefs = sharedPreferences.getBoolean("switch_delete", false);

        // Establecer la visibilidad del botón "Liberar" según el estado del Switch
        boolean isDeleteVisible = isDeleteEnabled || isDeleteEnabledFromPrefs;
        holder.itemView.findViewById(R.id.btnLiberar).setVisibility(isDeleteVisible ? View.VISIBLE : View.GONE);

        // Capitalizar la primera letra del nombre del Pokémon
        holder.pokemonName.setText(capitalizeFirstLetter(pokemon.getName()));
        holder.pokemonWeight.setText(pokemon.getWeight() > 0 ? context.getString(R.string.peso) + pokemon.getWeight() / 10 + "kg" : "Peso: Cargando...");
        holder.pokemonHeight.setText(pokemon.getHeight() > 0 ? context.getString(R.string.altura) + pokemon.getHeight() / 10 + "m" : "Altura: Cargando...");

        // Cargar la imagen del Pokémon con Glide
        String imageUrl = getPokemonImageUrl(pokemon.getUrl());
        Glide.with(context).load(imageUrl).into(holder.pokemonImage);

        // Configurar acción para mostrar detalles del Pokémon
        holder.itemView.setOnClickListener(v -> showPokemonDetail(pokemon));

        holder.itemView.findViewById(R.id.btnLiberar).setOnClickListener(v -> releasePokemon(position));
    }

    @Override
    public int getItemCount() {
        return capturedPokemonList.size();
    }

    /**
     * Método para liberar un Pokémon.
     * Elimina el Pokémon de la lista local y de Firestore.
     */
    private void releasePokemon(int position) {
        Pokemon pokemon = capturedPokemonList.get(position);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("Firestore", String.valueOf(R.string.usuarioNoIdentificado));
            return;
        }

        // Eliminar el Pokémon de Firestore
        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection("captured_pokemons")
                .document(String.valueOf(pokemon.getId()))
                .delete()
                .addOnSuccessListener(aVoid -> {
                    capturedPokemonList.remove(position); // Eliminar de la lista interna
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, capturedPokemonList.size());

                    // Notificar al listener que el Pokémon fue liberado
                    if (releaseListener != null) {
                        releaseListener.onPokemonReleased(pokemon);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", String.valueOf(R.string.errorEliminandoPokemon), e));
    }

    /**
     * Método para mostrar los detalles de un Pokémon.
     */
    private void showPokemonDetail(Pokemon pokemon) {
        if (context instanceof PokedexActivity) {
            PokedexActivity activity = (PokedexActivity) context;

            // Mostrar el contenedor de fragmentos
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
            viewPager.setVisibility(View.GONE);

            FrameLayout fragmentContainer = activity.findViewById(R.id.fragment_container);
            fragmentContainer.setVisibility(View.VISIBLE);

            // Reemplazar el fragmento
            PokemonDetailFragment detailFragment = PokemonDetailFragment.newInstance(pokemon);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Método para obtener la URL de la imagen de un Pokémon.
     */
    private String getPokemonImageUrl(String url) {
        String[] parts = url.split("/");
        String id = parts[parts.length - 1].isEmpty() ? parts[parts.length - 2] : parts[parts.length - 1];
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
    }

    /**
     * ViewHolder para el adaptador.
     */
    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        ImageView pokemonImage;
        TextView pokemonName, pokemonWeight, pokemonHeight;

        public PokemonViewHolder(@NonNull ItemPokemonCapturedBinding binding) {
            super(binding.getRoot()); // Asocia la raíz del binding con el ViewHolder
            pokemonImage = binding.ivPokemonImage;
            pokemonName = binding.tvName;
            pokemonWeight = binding.tvWeight;
            pokemonHeight = binding.tvHeight;
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
}
