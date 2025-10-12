package com.example.pokedex_repo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.widget.ImageView
import android.widget.TextView

class DetailActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvHabitat: TextView
    private lateinit var tvDescription: TextView
    private lateinit var ivImage: ImageView

    private val viewModel: PokemonDetailViewModel by viewModels {
        ViewModelFactory(RetrofitInstance.api)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        tvName = findViewById(R.id.tvName)
        tvHeight = findViewById(R.id.tvHeight)
        tvWeight = findViewById(R.id.tvWeight)
        tvHabitat = findViewById(R.id.tvHabitat)
        tvDescription = findViewById(R.id.tvDescription)
        ivImage = findViewById(R.id.ivImage)

        val name = intent.getStringExtra("name") ?: return
        viewModel.loadPokemon(name)

        lifecycleScope.launch {
            viewModel.pokemonDetail.collectLatest { detail ->
                if (detail.name.isNotEmpty()) {
                    tvName.text = detail.name.replaceFirstChar { it.uppercase() }
                    tvHeight.text = "Altura: ${detail.height} m"
                    tvWeight.text = "Peso: ${detail.weight} kg"
                    tvHabitat.text = "Hábitat: ${detail.habitat}"
                    tvDescription.text = detail.description

                    Glide.with(this@DetailActivity)
                        .load(detail.imageUrl)
                        .into(ivImage)
                }
            }
        }
    }
}
