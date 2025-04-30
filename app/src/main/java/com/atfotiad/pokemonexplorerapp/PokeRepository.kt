package com.atfotiad.pokemonexplorerapp

import android.util.Log
import com.atfotiad.pokemonexplorerapp.api.PokeClient
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import com.atfotiad.pokemonexplorerapp.utils.repository.RepoUtils.toResult
import com.atfotiad.pokemonexplorerapp.utils.repository.Result
import com.atfotiad.pokemonexplorerapp.utils.repository.Result.Error
import com.atfotiad.pokemonexplorerapp.utils.repository.Result.Loading
import com.atfotiad.pokemonexplorerapp.utils.repository.Result.Success
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ActivityRetainedScoped
class PokeRepository @Inject constructor(
    private val pokeApi: PokeClient
) {
    private val _totalPokemonCount = MutableStateFlow(0)
    val totalPokemonCount = _totalPokemonCount.asStateFlow()

    fun getPokemonByName(name: String): Flow<Result<Pokemon>> = flow {
        emit(Loading)
        val result = suspend { pokeApi.getPokemonInfoByName(name) }.toResult()
        emit(
            when (result) {
                is Success -> {
                    val response = result.data
                    if (response != null) {
                        val speciesParts = response.species.url.split("/")
                        val speciesId = speciesParts[speciesParts.size - 2].toInt()
                        val pokeDexEntryResult =
                            suspend { pokeApi.getSpeciesEntry(speciesId) }.toResult()
                        if (pokeDexEntryResult is Success && pokeDexEntryResult.data != null) {
                            val pokeDexEntryText = pokeDexEntryResult.data
                            val imageUrl = response.sprites.other.officialArtwork.frontDefault
                                ?: response.sprites.other.home.frontDefault
                                ?: "https://freesvg.org/img/Simple-Image-Not-Found-Icon.png"
                            Success(
                                Pokemon(
                                    response.id,
                                    response.name,
                                    response.types,
                                    response.stats,
                                    response.species,
                                    response.cries.latest,
                                    imageUrl,
                                    pokeDexEntryText.flavorTextEntries.firstOrNull {
                                        it.language.name == "en"
                                    }?.flavorText
                                        ?: ""
                                )
                            )
                        } else {
                            Success(null) // Species entry failed but pokemon info succeeded
                        }
                    } else {
                        Success(null) // Pokemon info was successful but body was null
                    }
                }

                is Error -> result
                Loading -> Loading // Should not happen here as we emit Loading outside
            }
        )
    }

    fun getPokemonList(offset: Int, limit: Int): Flow<Result<List<Pokemon>>> = flow {
        emit(Loading)
        val responseResult = suspend { pokeApi.getAllPokemon(offset, limit) }.toResult()
        emit(
            when (responseResult) {
                is Success -> {
                    val response = responseResult.data
                    if (response != null) {
                        _totalPokemonCount.value = response.count
                        Log.i("Pokemon", "getPokemonList: Next page is ${response.next}")
                        val pokemonList = mutableListOf<Pokemon>()
                        coroutineScope {
                            val pokemonDetails = response.results.map {
                                async {
                                    val urlParts = it.url.split("/")
                                    val id = urlParts[urlParts.size - 2].toInt()
                                    val pokemonResponseResult =
                                        suspend { pokeApi.getPokemonInfo(id) }.toResult()
                                    if (pokemonResponseResult is Success && pokemonResponseResult.data != null) {
                                        val pokemon = pokemonResponseResult.data
                                        val speciesParts = pokemon.species.url.split("/")
                                        val speciesId = speciesParts[speciesParts.size - 2].toInt()
                                        val pokeDexEntryResult =
                                            suspend { pokeApi.getSpeciesEntry(speciesId) }.toResult()
                                        if (pokeDexEntryResult is Success && pokeDexEntryResult.data != null) {
                                            val pokeDexEntryText = pokeDexEntryResult.data
                                            val imageUrl =
                                                pokemon.sprites.other.officialArtwork.frontDefault
                                                    ?: pokemon.sprites.other.home.frontDefault
                                                    ?: "https://freesvg.org/img/Simple-Image-Not-Found-Icon.png"
                                            Pokemon(
                                                pokemon.id,
                                                pokemon.name,
                                                pokemon.types,
                                                pokemon.stats,
                                                pokemon.species,
                                                pokemon.cries.latest,
                                                imageUrl,
                                                pokeDexEntryText.flavorTextEntries.firstOrNull { it.language.name == "en" }?.flavorText
                                                    ?: ""
                                            )
                                        } else {
                                            null
                                        }
                                    } else {
                                        null
                                    }
                                }
                            }
                            pokemonDetails.forEach { deferred ->
                                deferred.await()?.let { pokemon ->
                                    pokemonList.add(pokemon)
                                }
                            }
                        }
                        Success(pokemonList)
                    } else {
                        Success(emptyList())
                    }
                }

                is Error -> responseResult
                Loading -> Loading // Should not happen here
            }
        )
    }

}