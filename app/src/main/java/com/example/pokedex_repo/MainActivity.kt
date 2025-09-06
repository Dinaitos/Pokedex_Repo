package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val pokemonList = arrayListOf(
        Pokemon("Pikachu", "0.4m", "6kg", R.drawable.pikachu),
        Pokemon("Charmander", "0.6m", "8kg", R.drawable.charmander),
        Pokemon("Bulbasaur", "0.7m", "7kg", R.drawable.bulbasaur),
        Pokemon("Squirtle", "0.5m", "9kg", R.drawable.squirtle),
        Pokemon("Jigglypuff", "0.5m", "5kg", R.drawable.jigglypuff),
        Pokemon("Meowth", "0.4m", "4kg", R.drawable.meowth),
        Pokemon("Psyduck", "0.6m", "8kg", R.drawable.psyduck),
        Pokemon("Snorlax", "2.1m", "460kg", R.drawable.snorlax),
        Pokemon("Gengar", "1.5m", "40kg", R.drawable.gengar),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setLogo(R.drawable.pokedex)

        val rvPokemon: RecyclerView = findViewById(R.id.rvPokemon)
        rvPokemon.layoutManager = LinearLayoutManager(this)
        rvPokemon.adapter = PokemonAdapter(pokemonList) { pokemon ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("name", pokemon.name)
                putExtra("height", pokemon.height)
                putExtra("weight", pokemon.weight)
                putExtra("imageRes", pokemon.imageRes)

                putExtra("description", when(pokemon.name) {
                    "Pikachu" -> getString(R.string.descripcion_pika)
                    "Charmander" -> getString(R.string.descripcion_char)
                    else -> ""
                })
            }
            startActivity(intent)
        }
    }
}

