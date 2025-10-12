package com.example.pokedex_repo

import com.google.gson.annotations.SerializedName

// ---------------- API: Pokemon detail ----------------
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val types: List<TypeSlot>,
    val stats: List<StatSlot>,
    val species: NamedAPIResource
)

data class Sprites(
    val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String?
)

data class TypeSlot(
    val slot: Int,
    val type: NamedAPIResource
)

data class StatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedAPIResource
)

data class NamedAPIResource(
    val name: String,
    val url: String
)

// ---------------- API: species ----------------
data class PokemonSpeciesResponse(
    val habitat: Habitat?,
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>?,
    @SerializedName("evolution_chain")
    val evolutionChain: EvolutionChainRef?,
    @SerializedName("growth_rate")
    val growthRate: GrowthRate?
)

data class Habitat(val name: String?)
data class GrowthRate(val name: String?)
data class FlavorTextEntry(
    @SerializedName("flavor_text") val flavorText: String?,
    val language: NamedAPIResource?
)
data class EvolutionChainRef(val url: String?)

// ---------------- API: evolution chain ----------------
data class EvolutionChainResponse(
    val id: Int,
    val chain: EvolutionChainLink
)

data class EvolutionChainLink(
    val species: NamedAPIResource,
    @SerializedName("evolves_to")
    val evolvesTo: List<EvolutionChainLink> = emptyList(),
    @SerializedName("evolution_details")
    val evolutionDetails: List<EvolutionDetail> = emptyList()
)

data class EvolutionDetail(
    @SerializedName("min_level")
    val minLevel: Int?
)

// ---------------- UI models used by ViewModel / Activity ----------------
data class EvolutionStage(
    val name: String,
    val imageUrl: String,
    val minLevel: Int?
)

data class Stat(
    val name: String,
    val value: Int
)

data class PokemonDetailUi(
    val name: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val imageUrl: String = "",
    val types: List<String> = emptyList(),
    val stats: List<Stat> = emptyList(),
    val habitat: String = "",
    val growthRate: String = "",
    val description: String = "",
    val evolutionChain: List<EvolutionStage> = emptyList()
)
