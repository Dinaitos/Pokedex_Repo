package com.example.pokedex_repo

import com.google.gson.annotations.SerializedName

data class ChainLink(
    val species: SpeciesName,
    @SerializedName("evolves_to") val evolvesTo: List<ChainLink>
)

data class SpeciesName(
    val name: String
)
