package com.example.pokedex_repo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val height: String = "Desconocido",
    val weight: String = "Desconocido",
    val imageRes: Int = 0
)