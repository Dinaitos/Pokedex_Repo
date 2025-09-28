package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.pokedex_repo.R

class MainActivity : AppCompatActivity() {

    private lateinit var rvPokemon: RecyclerView
    private lateinit var adapter: PokemonAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar sesión antes de cargar la UI
        val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
        val sesionRecordada = prefs.getBoolean("recordarme", false)
        if (!sesionRecordada) {
            startActivity(Intent(this, Login_Activity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setLogo(R.drawable.pokedex)

        // RecyclerView
        rvPokemon = findViewById(R.id.rvPokemon)
        rvPokemon.layoutManager = LinearLayoutManager(this)

        adapter = PokemonAdapter(emptyList()) { pokemon ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("name", pokemon.name)
                putExtra("height", pokemon.height)
                putExtra("weight", pokemon.weight)
                putExtra("imageRes", pokemon.imageRes)
                putExtra(
                    "description",
                    when (pokemon.name) {
                        "Pikachu" -> getString(R.string.descripcion_pika)
                        "Charmander" -> getString(R.string.descripcion_char)
                        else -> ""
                    }
                )
            }
            startActivity(intent)
        }
        rvPokemon.adapter = adapter

        // Base de datos
        db = AppDatabase.getDatabase(this)

        // Cargar Pokémon
        lifecycleScope.launch {
            if (db.pokemonDao().getAll().isEmpty()) {
                db.pokemonDao().insertAll(
                    listOf(
                        Pokemon(name = "Pikachu", height = "0.4m", weight = "6kg", imageRes = R.drawable.pikachu),
                        Pokemon(name = "Charmander", height = "0.6m", weight = "8kg", imageRes = R.drawable.charmander),
                        Pokemon(name = "Bulbasaur", height = "0.7m", weight = "7kg", imageRes = R.drawable.bulbasaur),
                        Pokemon(name = "Squirtle", height = "0.5m", weight = "9kg", imageRes = R.drawable.squirtle),
                        Pokemon(name = "Jigglypuff", height = "0.5m", weight = "5kg", imageRes = R.drawable.jigglypuff),
                        Pokemon(name = "Meowth", height = "0.4m", weight = "4kg", imageRes = R.drawable.meowth),
                        Pokemon(name = "Psyduck", height = "0.6m", weight = "8kg", imageRes = R.drawable.psyduck),
                        Pokemon(name = "Snorlax", height = "2.1m", weight = "460kg", imageRes = R.drawable.snorlax),
                        Pokemon(name = "Gengar", height = "1.5m", weight = "40kg", imageRes = R.drawable.gengar)
                    )
                )
            }
            val pokemons = db.pokemonDao().getAll()
            adapter.updateData(pokemons)
        }
    }

    // Menú toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // Mostrar icono de cerrar sesión solo si hay sesión recordada
        val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
        val sesionRecordada = prefs.getBoolean("recordarme", false)
        menu?.findItem(R.id.action_cerrar_sesion)?.isVisible = sesionRecordada

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cerrar_sesion -> {
                // Borrar SharedPreferences
                val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
                prefs.edit().clear().apply()

                // Volver al login
                val intent = Intent(this, Login_Activity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
