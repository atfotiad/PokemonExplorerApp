package com.atfotiad.pokemonexplorerapp.data.repository

import com.atfotiad.pokemonexplorerapp.data.model.Pokemon
import com.atfotiad.pokemonexplorerapp.utils.repository.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
/**[PokeRepository] is an interface that defines the methods for retrieving Pokemon data.
 * It includes methods for retrieving a Pokemon by name and a list of Pokemon.
 * It also provides a StateFlow for observing the total count of Pokemon.
 * */
interface PokeRepository {
    fun getPokemonByName(name: String): Flow<Result<Pokemon>>
    fun getPokemonList(offset: Int, limit: Int): Flow<Result<List<Pokemon>>>
    val totalPokemonCount: StateFlow<Int>
}