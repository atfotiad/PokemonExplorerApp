package com.atfotiad.pokemonexplorerapp

import android.util.Log
import com.atfotiad.pokemonexplorerapp.api.PokeClient
import com.atfotiad.pokemonexplorerapp.model.Pokemon
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

    fun getPokemonList(offset: Int, limit: Int): Flow<List<Pokemon>> = flow {
        val response = pokeApi.getAllPokemon(offset, limit)
        if (response.isSuccessful && response.body() != null) {
            _totalPokemonCount.value = response.body()!!.count
            Log.i("Pokemon", "getPokemonList: Next page is ${response.body()?.next}")
            val pokemonList = mutableListOf<Pokemon>()
            coroutineScope {
                val pokemonDetails = response.body()!!.results.map {
                    async {
                        val urlParts = it.url.split("/")
                        val id = urlParts[urlParts.size - 2].toInt()
                        val pokemonResponse = pokeApi.getPokemonInfo(id)
                        if (pokemonResponse.isSuccessful && pokemonResponse.body() != null) {
                            val pokemon = pokemonResponse.body()!!
                            val speciesParts = pokemon.species.url.split("/")
                            val speciesId = speciesParts[speciesParts.size - 2].toInt()
                            val pokeDexEntryText = pokeApi.getSpeciesEntry(speciesId)
                            val imageUrl = pokemon.sprites.other.officialArtwork.frontDefault
                                ?: pokemon.sprites.other.home.frontDefault
                                ?: "https://freesvg.org/img/Simple-Image-Not-Found-Icon.png"

                            if (pokeDexEntryText.isSuccessful && pokeDexEntryText.body() != null) {
                                Pokemon(
                                    pokemon.id,
                                    pokemon.name,
                                    pokemon.types,
                                    pokemon.stats,
                                    pokemon.species,
                                    pokemon.cries.latest,
                                    imageUrl,
                                    pokeDexEntryText.body()!!.flavorTextEntries.first {
                                        it.language.name == "en"
                                    }.flavorText
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
            emit(pokemonList)
        } else {
            emit(emptyList())
        }
    }
}