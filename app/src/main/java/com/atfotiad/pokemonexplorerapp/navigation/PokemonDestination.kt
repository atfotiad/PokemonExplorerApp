package com.atfotiad.pokemonexplorerapp.navigation

import com.atfotiad.pokemonexplorerapp.data.model.Pokemon
import kotlinx.serialization.Serializable

/**
 * [PokemonDestination] is a sealed interface that defines the possible destinations in the app.]
 * */
sealed interface PokemonDestination {
    @Serializable
    data object MainScreen: PokemonDestination

    @Serializable
    data class DetailScreen(
        val pokemon: Pokemon
    ): PokemonDestination
}