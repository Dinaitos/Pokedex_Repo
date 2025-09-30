package com.example.pokedex_repo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class Pokemon(
    @PrimaryKey(autoGenerate = true) val id: Int? = null, // Cambié a Int? = null
    val name: String,
    val height: String,
    val weight: String,
    val imageRes: Int
)




