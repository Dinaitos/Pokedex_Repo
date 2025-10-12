package com.example.pokedex_repo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PokemonFullDetail(
    val name: String = "",
    val height: Float = 0f,
    val weight: Float = 0f,
    val imageUrl: String = "",
    val types: List<String> = emptyList(),
    val stats: Map<String, Int> = emptyMap(),
    val habitat: String = "",
    val growthRate: String = "",
    val description: String = "",
    val evolutions: List<String> = emptyList()
)

class PokemonDetailViewModel(
    private val api: PokeApiService
) : ViewModel() {

    private val _pokemonDetail = MutableStateFlow(PokemonFullDetail())
    val pokemonDetail: StateFlow<PokemonFullDetail> = _pokemonDetail

    fun loadPokemon(name: String) {
        viewModelScope.launch {
            try {
                // 1) llamadas suspend (Retrofit suspend functions)
                val detailResponse = api.getPokemonDetail(name)
                val speciesResponse = api.getPokemonSpecies(name)

                // 2) imagen, tipos y stats (seguro con safe calls)
                val imageUrl = detailResponse.sprites
                    ?.other
                    ?.officialArtwork
                    ?.frontDefault
                    ?: ""

                val typesList = detailResponse.types?.mapNotNull { it.type?.name } ?: emptyList()

                // stats: tratamos de leer baseStat o base_stat (la mayoría usa baseStat)
                val statsMap: Map<String, Int> = detailResponse.stats
                    ?.mapNotNull { statSlot ->
                        val statName = statSlot.stat?.name ?: return@mapNotNull null
                        // intentar ambos nombres posibles
                        val value = when {
                            // si la propiedad se llama baseStat
                            (statSlot::class.members.any { it.name == "baseStat" }) -> {
                                statSlot::class.members.first { it.name == "baseStat" }
                                    .call(statSlot) as? Int ?: 0
                            }
                            // si la propiedad se llama base_stat
                            (statSlot::class.members.any { it.name == "base_stat" }) -> {
                                statSlot::class.members.first { it.name == "base_stat" }
                                    .call(statSlot) as? Int ?: 0
                            }
                            else -> {
                                // fallback: tratar de acceder a "baseStat" con safe cast
                                try {
                                    val prop = statSlot::class.members.firstOrNull { it.name.contains("base") }
                                    prop?.call(statSlot) as? Int ?: 0
                                } catch (_: Exception) {
                                    0
                                }
                            }
                        }
                        statName to value
                    }?.toMap() ?: emptyMap()

                // 3) descripción (flavor text) en inglés
                val description = speciesResponse.flavorTextEntries
                    ?.firstOrNull { it.language?.name == "en" }
                    ?.flavorText
                    ?.replace("\n", " ")
                    ?.replace("\u000c", " ")
                    ?: "No description available."

                // 4) habitat y growth rate
                val habitat = speciesResponse.habitat?.name ?: "Unknown"
                val growth = speciesResponse.growthRate?.name ?: "Unknown"

                // 5) cadena de evolución (usar URL que devuelve species.evolutionChain.url)
                val evolutions = mutableListOf<String>()
                val evoUrl = speciesResponse.evolutionChain?.url
                val evoId = evoUrl
                    ?.split("/")
                    ?.filter { it.isNotEmpty() }
                    ?.lastOrNull()
                    ?.toIntOrNull()

                if (evoId != null) {
                    val evoResponse = api.getEvolutionChain(evoId)
                    // recorrer recursivamente o iterativamente
                    var current = evoResponse.chain
                    // primer nodo y siguientes
                    if (current != null) {
                        evolutions.add(current.species?.name ?: "")
                        while (!current.evolvesTo.isNullOrEmpty()) {
                            current = current.evolvesTo.first()
                            evolutions.add(current.species?.name ?: "")
                        }
                    }
                }

                // 6) actualizar estado
                _pokemonDetail.value = PokemonFullDetail(
                    name = detailResponse.name ?: "",
                    height = (detailResponse.height ?: 0f) / 10f,
                    weight = (detailResponse.weight ?: 0f) / 10f,
                    imageUrl = imageUrl,
                    types = typesList,
                    stats = statsMap,
                    habitat = habitat,
                    growthRate = growth,
                    description = description,
                    evolutions = evolutions
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
