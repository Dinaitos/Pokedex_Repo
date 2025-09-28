package com.example.pokedex_repo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: Pokemon)

    @Insert
    suspend fun insertAll(pokemons: List<Pokemon>)

    @Query("SELECT * FROM pokemon")
    suspend fun getAll(): List<Pokemon>

    @Query("DELETE FROM pokemon")
    suspend fun deleteAll()
}
