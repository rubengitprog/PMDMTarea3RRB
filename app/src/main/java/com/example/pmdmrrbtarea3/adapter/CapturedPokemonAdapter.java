package com.example.pmdmrrbtarea3.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.pmdmrrbtarea3.R;
import com.example.pmdmrrbtarea3.ToastUtils;
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
    private final PokemonReleaseListener releaseListener;
    private boolean isDeleteEnabled;

    public CapturedPokemonAdapter(Context context, ArrayList<Pokemon> capturedPokemonList, PokemonReleaseListener releaseListener) {
        this.context = context;
        this.capturedPokemonList = capturedPokemonList;
        this.releaseListener = releaseListener;
        this.isDeleteEnabled = false; // Inicializamos con el valor predeterminado
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
        //  boolean isDeleteVisible = isDeleteEnabled || isDeleteEnabledFromPrefs;

        // Configurar visibilidad del botón
        //    holder.itemView.findViewById(R.id.btnLiberar).setVisibility(isDeleteVisible ? View.VISIBLE : View.GONE);

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

    private void releasePokemon(int position) {
        if (position < 0 || position >= capturedPokemonList.size()) {
            return;
        }

        Pokemon pokemon = capturedPokemonList.get(position);

        // Mostrar el cuadro de diálogo de confirmación
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirmaci_n)
                .setMessage(context.getString(R.string.est_s_seguro_de_que_quieres_liberar_a) + capitalizeFirstLetter(pokemon.getName()) + "?")
                .setPositiveButton((context.getString(R.string.liberar)), (dialog, which) -> liberarPokemonDesdeFirestore(position, pokemon))
                .setNegativeButton((context.getString(R.string.cancelar)), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void liberarPokemonDesdeFirestore(int position, Pokemon pokemon) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("Firestore", context.getString(R.string.usuarioNoIdentificado));
            return;
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection("captured_pokemons")
                .document(String.valueOf(pokemon.getName()))
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Elimina el Pokémon de la lista local y actualiza el RecyclerView
                    if (position < 0 || position >= capturedPokemonList.size()) {
                        Log.e("ReleasePokemon", "Posición inválida después de eliminar en Firestore: " + position);
                        return;
                    }
                    capturedPokemonList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, capturedPokemonList.size());

                    // Muestra un Toast para confirmar la eliminación
                    if (context instanceof PokedexActivity) {
                        ((PokedexActivity) context).runOnUiThread(() ->
                                ToastUtils.showCustomToast(context, capitalizeFirstLetter(pokemon.getName()) + " " + context.getString(R.string.liberado))
                        );
                    }

                    // Llama al listener si está configurado
                    if (releaseListener != null) {
                        releaseListener.onPokemonReleased(pokemon);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", context.getString(R.string.errorEliminandoPokemon), e);
                });
    }

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

    private String getPokemonImageUrl(String url) {
        String[] parts = url.split("/");
        String id = parts[parts.length - 1].isEmpty() ? parts[parts.length - 2] : parts[parts.length - 1];
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        ImageView pokemonImage;
        TextView pokemonName, pokemonWeight, pokemonHeight;

        public PokemonViewHolder(@NonNull ItemPokemonCapturedBinding binding) {
            super(binding.getRoot());
            pokemonImage = binding.ivPokemonImage;
            pokemonName = binding.tvName;
            pokemonWeight = binding.tvWeight;
            pokemonHeight = binding.tvHeight;
        }
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
