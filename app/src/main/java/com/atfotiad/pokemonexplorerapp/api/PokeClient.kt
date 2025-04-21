package com.atfotiad.pokemonexplorerapp.api

import com.atfotiad.pokemonexplorerapp.model.BaseResponse
import com.atfotiad.pokemonexplorerapp.model.PokeDexEntryText
import com.atfotiad.pokemonexplorerapp.model.PokemonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeClient {

    @GET("pokemon")
    suspend fun getAllPokemon(
        @Query("offset")
        offset: Int = 0,
        @Query("limit")
        limit: Int = 10
    ): Response<BaseResponse>


    @GET("pokemon/{id}")
    suspend fun getPokemonInfo(
        @Path("id") id: Int
    ): Response<PokemonResponse>

    @GET("pokemon-species/{id}")
    suspend fun getSpeciesEntry(
        @Path("id") id: Int
    ): Response<PokeDexEntryText>

}