package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var rvPokemon: RecyclerView
    private lateinit var adapter: PokemonAdapter
    private val pokemonList = mutableListOf<PokemonResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
        val logueado = prefs.getBoolean("logueado", false)
        if (!logueado) {
            startActivity(Intent(this, Login_Activity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setLogo(R.drawable.pokedex)

        rvPokemon = findViewById(R.id.rvPokemon)
        rvPokemon.layoutManager = LinearLayoutManager(this)
        adapter = PokemonAdapter(pokemonList) { pokemon ->
            fetchPokemonDetails(pokemon.name)
        }
        rvPokemon.adapter = adapter

        lifecycleScope.launch { fetchPokemons() }
    }

    private suspend fun fetchPokemons() {
        try {
            val response = RetrofitInstance.api.getPokemons(151)
            pokemonList.clear()
            pokemonList.addAll(response.results)
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Log.e("API", "Error al obtener pokemons: ${e.message}")
        }
    }

    private fun fetchPokemonDetails(name: String) {
        lifecycleScope.launch {
            try {
                val detail = RetrofitInstance.api.getPokemonDetail(name)
                val species = RetrofitInstance.api.getPokemonSpecies(name)

                // DESCRIPCION: usar los nombres que tenés en PokemonSpeciesResponse
                val description = species.flavorTextEntries
                    .firstOrNull { it.language.name == "en" }
                    ?.flavorText
                    ?.replace("\n", " ")
                    ?.replace("\u000c", " ")
                    ?: "Sin descripción"

                // HABITAT
                val habitat = species.habitat?.name ?: "Desconocido"

                // IMAGEN: usar la propiedad frontDefault definida en tus data classes
                val imageUrl = detail.sprites.other.officialArtwork.frontDefault

                val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
                    putExtra("name", detail.name)
                    putExtra("height", "${detail.height / 10} m")
                    putExtra("weight", "${detail.weight / 10} kg")
                    putExtra("description", description)
                    putExtra("habitat", habitat)
                    putExtra("imageUrl", imageUrl)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("DETAIL", "Error al traer detalle: ${e.message}")
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
        val sesionRecordada = prefs.getBoolean("recordarme", false)
        menu?.findItem(R.id.action_cerrar_sesion)?.isVisible = sesionRecordada
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cerrar_sesion -> {
                val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
                prefs.edit().clear().apply()
                val intent = Intent(this, Login_Activity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
