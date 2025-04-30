package com.atfotiad.pokemonexplorerapp

import com.atfotiad.pokemonexplorerapp.data.source.remote.PokeRemoteDataSource
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import com.atfotiad.pokemonexplorerapp.model.PokemonResponse
import com.atfotiad.pokemonexplorerapp.utils.repository.Result
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ActivityRetainedScoped
@Suppress("UNCHECKED_CAST")
class PokeRepository @Inject constructor(
    private val remoteDataSource: PokeRemoteDataSource
) {
    private val _totalPokemonCount = MutableStateFlow(0)
    val totalPokemonCount = _totalPokemonCount.asStateFlow()


    fun getPokemonByName(name: String): Flow<Result<Pokemon>> = flow {
        emit(Result.Loading)
        val pokemonInfoResult = remoteDataSource.getPokemonInfo(name)
        if (pokemonInfoResult is Result.Success) {
            emit(fetchSpeciesAndCreatePokemon(pokemonInfoResult.data))
        } else {
            emit(pokemonInfoResult as Result<Pokemon>)
        }
    }

    fun getPokemonList(offset: Int, limit: Int): Flow<Result<List<Pokemon>>> = flow {
        emit(Result.Loading)
        val pokemonListResponseResult = remoteDataSource.getPokemonList(offset, limit)
        if (pokemonListResponseResult is Result.Success) {
            pokemonListResponseResult.data?.let { response ->
                _totalPokemonCount.value = response.count
                val pokemonList = mutableListOf<Pokemon>()
                coroutineScope {
                    val pokemonDetailsDeferred = response.results.map { basicPokemon ->
                        async {
                            val id = basicPokemon.url.split("/").dropLast(1).last().toInt()
                            val pokemonInfoResult = remoteDataSource.getPokemonInfo(id)
                            if (pokemonInfoResult is Result.Success) {
                                fetchSpeciesAndCreatePokemon(pokemonInfoResult.data)
                            } else {
                                null
                            }
                        }
                    }
                    pokemonDetailsDeferred.forEach { deferred ->
                        (deferred.await() as? Result.Success)?.data?.let { pokemonList.add(it) }
                    }
                }
                emit(Result.Success(pokemonList))
            } ?: emit(Result.Success(emptyList()))
        } else {
            emit(pokemonListResponseResult as Result<List<Pokemon>>)
        }
    }

    private suspend fun fetchSpeciesAndCreatePokemon(pokemonResponse: PokemonResponse?): Result<Pokemon> {
        return pokemonResponse?.let {
            val speciesId = it.species.url.split("/").dropLast(1).last().toInt()
            val speciesEntryResult = remoteDataSource.getSpeciesEntry(speciesId)
            val flavorText =
                (speciesEntryResult as? Result.Success)?.data?.flavorTextEntries?.firstOrNull { entry -> entry.language.name == "en" }?.flavorText
                    ?: ""
            val imageUrl = it.sprites.other.officialArtwork.frontDefault
                ?: it.sprites.other.home.frontDefault
                ?: "https://freesvg.org/img/Simple-Image-Not-Found-Icon.png"
            Result.Success(
                Pokemon(
                    it.id,
                    it.name,
                    it.types,
                    it.stats,
                    it.species,
                    it.cries.latest,
                    imageUrl,
                    flavorText
                )
            )
        } ?: Result.Success(null)
    }
}