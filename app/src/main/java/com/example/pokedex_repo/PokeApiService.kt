package com.example.pokedex_repo

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    fun getPokemons(@Query("limit") limit: Int = 151): Call<PokemonResponse>
}
