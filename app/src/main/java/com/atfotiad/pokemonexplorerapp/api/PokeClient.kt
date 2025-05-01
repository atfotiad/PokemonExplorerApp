package com.atfotiad.pokemonexplorerapp.api

import com.atfotiad.pokemonexplorerapp.data.model.BaseResponse
import com.atfotiad.pokemonexplorerapp.data.model.PokeDexEntryText
import com.atfotiad.pokemonexplorerapp.data.model.PokemonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import kotlin.jvm.Throws

interface PokeClient {

    @GET("pokemon")
    @Throws(IOException::class)
    suspend fun getAllPokemon(
        @Query("offset")
        offset: Int = 0,
        @Query("limit")
        limit: Int = 10
    ): Response<BaseResponse>


    @GET("pokemon/{id}")
    @Throws(IOException::class)
    suspend fun getPokemonInfo(@Path("id") id: Int): Response<PokemonResponse>

    @GET("pokemon/{name}")
    @Throws(IOException::class)
    suspend fun getPokemonInfoByName(
        @Path("name") name: String
    ): Response<PokemonResponse>

    @GET("pokemon-species/{id}")
    @Throws(Exception::class)
    suspend fun getSpeciesEntry(
        @Path("id") id: Int
    ): Response<PokeDexEntryText>

}