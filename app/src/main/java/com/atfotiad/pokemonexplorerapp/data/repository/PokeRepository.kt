package com.atfotiad.pokemonexplorerapp.data.repository

import com.atfotiad.pokemonexplorerapp.data.model.Pokemon
import com.atfotiad.pokemonexplorerapp.utils.repository.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PokeRepository {
    fun getPokemonByName(name: String): Flow<Result<Pokemon>>
    fun getPokemonList(offset: Int, limit: Int): Flow<Result<List<Pokemon>>>
    val totalPokemonCount: StateFlow<Int>
}