package com.example.pokedex_repo

import com.google.gson.annotations.SerializedName

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Float,
    val weight: Float,
    val sprites: Sprites,
    val species: Species,
    val types: List<TypeSlot>,
    val stats: List<StatSlot>
)

// --- Tipos de Pokémon ---
data class TypeSlot(
    val slot: Int,
    val type: TypeInfo
)

data class TypeInfo(
    val name: String,
    val url: String
)

// --- Estadísticas ---
data class StatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: StatInfo
)

data class StatInfo(
    val name: String,
    val url: String
)

// --- Sprites ---
data class Sprites(
    val other: OtherSprites
)

data class OtherSprites(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork
)

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String
)

// --- Especie ---
data class Species(
    val name: String,
    val url: String
)

