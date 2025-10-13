package com.example.pokedex_repo

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private val viewModel: PokemonDetailViewModel by viewModels {
        ViewModelFactory(RetrofitInstance.api)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Referencias UI
        val ivImage = findViewById<ImageView>(R.id.ivImage)
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvHeight = findViewById<TextView>(R.id.tvHeight)
        val tvWeight = findViewById<TextView>(R.id.tvWeight)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val typesContainer = findViewById<LinearLayout>(R.id.tvTypes)
        val statsContainer = findViewById<LinearLayout>(R.id.statsContainer)
        val evoContainer = findViewById<LinearLayout>(R.id.tvEvolutionsContainer)

        val name = intent.getStringExtra("name") ?: return
        viewModel.loadPokemon(name)

        lifecycleScope.launch {
            viewModel.pokemonDetail.collectLatest { detail ->
                tvName.text = detail.name.replaceFirstChar { it.uppercase() }
                tvHeight.text = "Altura: ${detail.height} m"
                tvWeight.text = "Peso: ${detail.weight} kg"
                tvDescription.text = detail.description

                Glide.with(this@DetailActivity)
                    .load(detail.imageUrl)
                    .into(ivImage)

                // --- Tipos (chips) ---
                typesContainer.removeAllViews()
                detail.types.forEach { type ->
                    val chip = TextView(this@DetailActivity).apply {
                        text = type.replaceFirstChar { it.uppercase() }
                        setPadding(20, 8, 20, 8)
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        background = ContextCompat.getDrawable(context, R.drawable.type_chip_bg)
                        val lp = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        lp.setMargins(8, 0, 8, 0)
                        layoutParams = lp
                    }
                    chip.background?.setTint(
                        ContextCompat.getColor(this@DetailActivity, getColorForType(type))
                    )
                    typesContainer.addView(chip)
                }

                // --- Estadísticas con barras ---
                statsContainer.removeAllViews()
                val maxStat = 255 // valor máximo posible

                detail.stats.forEach { (name, value) ->
                    val statView = layoutInflater.inflate(R.layout.item_stat, statsContainer, false)

                    val tvStatName = statView.findViewById<TextView>(R.id.tvStatName)
                    val progressBar = statView.findViewById<ProgressBar>(R.id.progressBar)
                    val tvStatValue = statView.findViewById<TextView>(R.id.tvStatValue)

                    tvStatName.text = name.replaceFirstChar { it.uppercase() }
                    tvStatValue.text = value.toString()
                    progressBar.max = maxStat
                    progressBar.progress = value

                    statsContainer.addView(statView)
                }

                // --- Evoluciones ---
                evoContainer.removeAllViews()
                if (detail.evolutionChain.isNotEmpty()) {
                    detail.evolutionChain.forEachIndexed { index, evo ->
                        val evoView = layoutInflater.inflate(R.layout.item_evolution, evoContainer, false)
                        val evoIv = evoView.findViewById<ImageView>(R.id.ivEvolution)
                        val evoNameTv = evoView.findViewById<TextView>(R.id.tvEvolutionName)
                        val evoLevelTv = evoView.findViewById<TextView>(R.id.tvEvolutionLevel)

                        evoNameTv.text = evo.name.replaceFirstChar { it.uppercase() }
                        evoLevelTv.text = evo.minLevel?.let { "Lvl $it" } ?: ""

                        Glide.with(this@DetailActivity)
                            .load(evo.imageUrl)
                            .into(evoIv)

                        evoContainer.addView(evoView)

                        if (index < detail.evolutionChain.lastIndex) {
                            val arrowTv = TextView(this@DetailActivity).apply {
                                text = "→"
                                textSize = 28f
                                val lp = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                lp.gravity = Gravity.CENTER_VERTICAL
                                lp.setMargins(12, 0, 12, 0)
                                layoutParams = lp
                            }
                            evoContainer.addView(arrowTv)
                        }
                    }
                } else {
                    val noEvo = TextView(this@DetailActivity).apply {
                        text = "Sin evoluciones"
                        textSize = 16f
                        gravity = Gravity.CENTER
                    }
                    evoContainer.addView(noEvo)
                }

                // --- Color de fondo por tipo ---
                if (detail.types.isNotEmpty()) {
                    window.decorView.setBackgroundColor(
                        ContextCompat.getColor(this@DetailActivity, getColorForType(detail.types.first()))
                    )
                }
            }
        }
    }

    private fun getColorForType(type: String): Int {
        return when (type.lowercase()) {
            "normal" -> R.color.normal
            "fire" -> R.color.fire
            "water" -> R.color.water
            "grass" -> R.color.grass
            "flying" -> R.color.flying
            "fighting" -> R.color.fighting
            "poison" -> R.color.poison
            "electric" -> R.color.electric
            "ground" -> R.color.ground
            "rock" -> R.color.rock
            "psychic" -> R.color.psychic
            "ice" -> R.color.ice
            "bug" -> R.color.bug
            "ghost" -> R.color.ghost
            "dragon" -> R.color.dragon
            "dark" -> R.color.dark
            "fairy" -> R.color.fairy
            "steel" -> R.color.steel
            else -> R.color.gray
        }

    }
}