package com.atfotiad.pokemonexplorerapp

import com.atfotiad.pokemonexplorerapp.api.PokeClient
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ActivityRetainedScoped
class PokeRepository @Inject constructor(
    private val pokeApi: PokeClient
) {
    
    fun getPokemonList(): Flow<List<Pokemon>> = flow {
        val response = pokeApi.getAllPokemon()
        if (response.isSuccessful && response.body() != null) {
            val pokemonList = mutableListOf<Pokemon>()
            coroutineScope {
                val pokemonDetails = response.body()!!.results.map {
                    async {
                        val urlParts = it.url.split("/")
                        val id = urlParts[urlParts.size - 2].toInt()
                        val pokemonResponse = pokeApi.getPokemonInfo(id)
                        if (pokemonResponse.isSuccessful && pokemonResponse.body() != null) {
                            val pokemon = pokemonResponse.body()!!
                            val pokeDexEntryText = pokeApi.getSpeciesEntry(pokemon.id)
                            if (pokeDexEntryText.isSuccessful && pokeDexEntryText.body() != null) {
                                Pokemon(
                                    pokemon.id,
                                    pokemon.name,
                                    pokemon.types,
                                    pokemon.stats,
                                    pokemon.cries.latest,
                                    pokemon.sprites.other.officialArtwork.frontDefault,
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