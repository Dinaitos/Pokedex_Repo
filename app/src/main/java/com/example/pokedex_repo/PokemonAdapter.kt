package com.example.pokedex_repo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PokemonAdapter(
    private val pokemonList: List<PokemonResult>,
    private val onItemClick: (PokemonResult) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvPokemonName)
        val ivImage: ImageView = view.findViewById(R.id.ivPokemonImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemonList[position]

        // Nombre con mayúscula inicial
        holder.tvName.text = pokemon.name.replaceFirstChar { it.uppercase() }

        // Obtener el número del Pokémon
        val id = pokemon.url.split("/").filter { it.isNotEmpty() }.last()

        // Cargar imagen desde la API oficial de sprites
        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.pokeball) // imagen por defecto
            .into(holder.ivImage)

        // Click listener
        holder.itemView.setOnClickListener { onItemClick(pokemon) }
    }

    override fun getItemCount(): Int = pokemonList.size
}



