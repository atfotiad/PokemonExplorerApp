package com.atfotiad.pokemonexplorerapp.data.source.remote

import com.atfotiad.pokemonexplorerapp.api.PokeClient
import com.atfotiad.pokemonexplorerapp.data.model.BaseResponse
import com.atfotiad.pokemonexplorerapp.data.model.PokeDexEntryText
import com.atfotiad.pokemonexplorerapp.data.model.PokemonResponse
import com.atfotiad.pokemonexplorerapp.utils.repository.RepoUtils.toResult
import com.atfotiad.pokemonexplorerapp.utils.repository.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokeRemoteDataSource @Inject constructor(
    private val pokeApi: PokeClient
) {
    suspend fun getPokemonList(offset: Int, limit: Int): Result<BaseResponse> {
        return suspend { pokeApi.getAllPokemon(offset, limit) }.toResult()
    }

    suspend fun getPokemonInfo(name: String): Result<PokemonResponse> {
        return suspend { pokeApi.getPokemonInfoByName(name) }.toResult()
    }

    suspend fun getPokemonInfo(id: Int): Result<PokemonResponse> {
        return suspend { pokeApi.getPokemonInfo(id) }.toResult()
    }

    suspend fun getSpeciesEntry(speciesId: Int): Result<PokeDexEntryText> {
        return suspend { pokeApi.getSpeciesEntry(speciesId) }.toResult()
    }
}