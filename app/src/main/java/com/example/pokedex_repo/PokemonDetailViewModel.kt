package com.example.pokedex_repo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val api: PokeApiService
) : ViewModel() {

    private val _pokemonDetail = MutableStateFlow(PokemonDetailUi())
    val pokemonDetail: StateFlow<PokemonDetailUi> = _pokemonDetail

    fun loadPokemon(nameOrId: String) {
        viewModelScope.launch {
            try {
                // 1) detalle y especie (API suspends assumed)
                val detail: PokemonDetailResponse = api.getPokemonDetail(nameOrId)
                val species: PokemonSpeciesResponse = api.getPokemonSpecies(nameOrId)

                // 2) tipos
                val types = detail.types.mapNotNull { it.type.name }

                // 3) stats
                val stats = detail.stats.map { Stat(it.stat.name, it.baseStat) }

                // 4) descripcion
                val description = species.flavorTextEntries
                    ?.firstOrNull { it.language?.name == "es" }
                    ?.flavorText
                    ?.replace("\n", " ")
                    ?.replace("\u000c", " ")
                    ?: "Sin descripción."

                // 5) imagen principal (official artwork)
                val imageUrl = detail.sprites.other?.officialArtwork?.frontDefault
                    ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/0.png"

                // 6) habitat y growthRate
                val habitat = species.habitat?.name ?: "Desconocido"
                val growth = species.growthRate?.name ?: "Unknown"

                // 7) evoluciones: traer chain si species.evolutionChain.url existe
                val evolutions = mutableListOf<EvolutionStage>()
                val evoUrl = species.evolutionChain?.url
                val evoId = evoUrl?.split("/")?.filter { it.isNotEmpty() }?.lastOrNull()?.toIntOrNull()
                if (evoId != null) {
                    val evoResponse: EvolutionChainResponse = api.getEvolutionChain(evoId)
                    // recorrer la cadena iterativamente (obtener species.name y minLevel)
                    var current: EvolutionChainLink? = evoResponse.chain
                    while (current != null) {
                        val evoName = current.species.name
                        // obtener id del species.url (para construir image)
                        val evoIdFromUrl = current.species.url.split("/").filter { it.isNotEmpty() }.lastOrNull()
                        val evoImage = if (!evoIdFromUrl.isNullOrBlank()) {
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$evoIdFromUrl.png"
                        } else {
                            ""
                        }
                        val minLevel = current.evolutionDetails.firstOrNull()?.minLevel
                        evolutions.add(EvolutionStage(evoName, evoImage, minLevel))
                        current = current.evolvesTo.firstOrNull()
                    }
                }

                // 8) armar UI model
                val ui = PokemonDetailUi(
                    name = detail.name,
                    height = detail.height / 10.0,
                    weight = detail.weight / 10.0,
                    imageUrl = imageUrl,
                    types = types,
                    stats = stats,
                    habitat = habitat,
                    growthRate = growth ?: "",
                    description = description,
                    evolutionChain = evolutions
                )

                _pokemonDetail.value = ui
            } catch (e: Exception) {
                e.printStackTrace()
                _pokemonDetail.value = PokemonDetailUi(
                    name = nameOrId,
                    description = "Error: ${e.localizedMessage ?: e.message}"
                )
            }
        }
    }
}
