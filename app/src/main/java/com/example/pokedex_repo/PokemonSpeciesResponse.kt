package com.example.pokedex_repo

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesResponse(
    val habitat: Habitat?,
    @SerializedName("growth_rate") val growthRate: GrowthRate,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>,
    @SerializedName("evolution_chain") val evolutionChain: EvolutionChainUrl
)

data class Habitat(
    val name: String
)

data class GrowthRate(
    val name: String
)

data class FlavorTextEntry(
    @SerializedName("flavor_text") val flavorText: String,
    val language: Language
)

data class Language(
    val name: String
)

data class EvolutionChainUrl(
    val url: String
)
