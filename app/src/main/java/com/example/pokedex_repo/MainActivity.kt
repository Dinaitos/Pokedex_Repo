package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET


class MainActivity : AppCompatActivity() {

    private lateinit var rvPokemon: RecyclerView
    private lateinit var adapter: PokemonAdapter
    private lateinit var db: AppDatabase
    private val pokemonList = mutableListOf<PokemonResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar sesión antes de cargar la UI
        val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
        val logueado = prefs.getBoolean("logueado", false)
        if (!logueado) {
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
        adapter = PokemonAdapter(pokemonList) { pokemon ->
            // Cuando se hace clic en un Pokémon
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("name", pokemon.name)
                putExtra("description", "Información del Pokémon obtenida desde la API.")
            }
            startActivity(intent)
        }
        rvPokemon.adapter = adapter

        // 🌐 Cargar Pokémon desde la API
        fetchPokemons()
    }

    // 🔄 Llamada a la API con Retrofit
    private fun fetchPokemons() {
        RetrofitInstance.api.getPokemons(151).enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        pokemonList.clear()
                        pokemonList.addAll(it.results)
                        adapter.notifyDataSetChanged()

                        // Guardar en BD si querés mantenerlos
                        lifecycleScope.launch {
                            it.results.forEach { p: PokemonResult ->
                                Log.d("POKEMON", "Guardando: ${p.name}")
                            }
                        }
                    }
                } else {
                    Log.e("API", "Error en respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                Log.e("API_ERROR", "Error al obtener pokemons: ${t.message}")
            }
        })
    }

    // Menú toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
        val sesionRecordada = prefs.getBoolean("recordarme", false)
        menu?.findItem(R.id.action_cerrar_sesion)?.isVisible = sesionRecordada

        return true
    }

    // Cerrar sesión
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